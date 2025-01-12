package io.github.windedge.viform.compose

import androidx.compose.runtime.*
import io.github.windedge.viform.core.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlin.reflect.*

@Composable
public fun <T : Any> FormHost<T>.useForm(content: @Composable FormScope<T>.(T) -> Unit) {
    val state = this.stateFlow.collectAsState()
    FormScope(this).content(state.value)
}

@Composable
public fun <T : Any> Form<T>.use(content: @Composable FormScope<T>.(T) -> Unit) {
    val formHost = DefaultFormHost(this)
    formHost.useForm(content)
}

public class FormScope<T : Any>(private val formHost: FormHost<T>) {
    private val form: Form<T> = formHost.form

    public val stateFlow: StateFlow<T> = formHost.stateFlow

    @Composable
    public fun <V : Any?> field(property: KProperty0<V>): FormField<V> {
        return form.getOrRegisterField(property)
    }

    @Composable
    public inline fun <reified V> field(
        property: KProperty0<V>, noinline content: @Composable FieldScope<T, V>.() -> Unit
    ) {
        val wrappedType = typeOf<V>()
        field(property).wrapAsType(wrappedType, { it }, { it }, content = content)
    }

    @Composable
    public inline fun <reified V, reified R> FormField<V>.wrapAs(
        noinline wrap: FormField<V>.(V) -> R,
        noinline unwrap: FormField<V>.(R) -> V,
        noinline constraints: ValidatorContainer<R>.() -> Unit = {},
        noinline content: @Composable FieldScope<T, R>.() -> Unit,
    ) {
        val wrappedType = typeOf<R>()
        this.wrapAsType(wrappedType, wrap, unwrap, constraints, content)
    }

    @Composable
    public fun <V, R> FormField<V>.wrapAsType(
        wrappedType: KType,
        wrap: FormField<V>.(V) -> R,
        unwrap: FormField<V>.(R) -> V,
        constraints: ValidatorContainer<R>.() -> Unit = {},
        content: @Composable() (FieldScope<T, R>.() -> Unit),
    ) {
        @Suppress("UNCHECKED_CAST")
        val wrappedFormField = this as? WrappedFormField<V, R> ?: remember(this.name) {
            WrappedFormField(this, wrappedType, wrap, unwrap).apply {
                form.replaceField(this)
                applyConstraints(constraints)
            }
        }
        if (wrappedFormField.wrappedType != wrappedType) {
            error("Can't wrap as another type!")
        }

        DisposableEffect(name) {
            onDispose { form.replaceField(wrappedFormField.origin) }
        }

        val resultState = this.resultFlow.collectAsState(ValidateResult.None)
        val fieldScope: FieldScope<T, R> = with(wrappedFormField) {
            FieldScope(
                wrappedState.value,
                resultState.value,
                ::setWrappedSate,
                ::validate,
                ::showError,
                formHost::pop
            )
        }
        fieldScope.content()
    }

}

public class FieldScope<T : Any, V : Any?>(
    value: V,
    result: ValidateResult,
    private val onValueChanged: (V, Boolean) -> Unit,
    private val onValidate: () -> Boolean,
    private val onShowError: (String) -> Unit,
    private val onSubmitted: () -> Unit,
) {
    public val currentValue: V = value
    public val hasError: Boolean = result.isError
    public val errorMessage: String? = result.errorMessage


    public fun setValue(value: V, validate: Boolean = false, submit: Boolean = false) {
        onValueChanged(value, validate)
        if (submit) {
            onSubmitted()
        }
    }

    public fun submit() {
        onSubmitted()
    }

    public fun validate(): Boolean {
        return onValidate()
    }

    public fun showError(message: String) {
        onShowError(message)
    }

    @OptIn(FlowPreview::class)
    @Composable
    public fun watchLazily(debouncedTimeoutMills: Long = 800, block: (V) -> Unit) {
        LaunchedEffect(currentValue) {
            snapshotFlow { currentValue }.distinctUntilChanged().debounce(debouncedTimeoutMills).collectLatest {
                block(it)
            }
        }
    }
}

internal class WrappedFormField<V, R>(
    val origin: FormField<V>,
    val wrappedType: KType,
    val wrap: FormField<V>.(V) -> R,
    val unwrap: FormField<V>.(R) -> V
) : FormField<V> by origin {

    private val wrappedValidators = mutableListOf<FieldValidator<R>>()

    private val _wrappedState: MutableState<R> = mutableStateOf(wrap(currentValue))
    val wrappedState: State<R> get() = _wrappedState

    override fun setValue(value: V, validate: Boolean) {
        origin.setValue(value, false)
        _wrappedState.value = wrap(value)

        if (validate) validate()
    }

    override fun validate(): Boolean {
        val results = listOf(
            origin.getValidators().validateAndGet(unwrap(_wrappedState.value)),
            wrappedValidators.validateAndGet(_wrappedState.value)
        )

        return results.find { it.isError }?.also { origin.setResult(it) }?.isSuccess ?: true
    }

    fun setWrappedSate(value: R, validate: Boolean = false) {
        _wrappedState.value = value
        val unwrapValue = unwrap(value)
        origin.setValue(unwrapValue, false)

        if (validate) validate()
    }

    fun applyConstraints(constraints: ValidatorContainer<R>.() -> Unit) {
        val container = SimpleValidatorContainer<R>().apply(constraints)
        wrappedValidators.addAll(container.getValidators())
    }

}

internal class SimpleValidatorContainer<V> : ValidatorContainer<V> {
    private val validators = mutableListOf<FieldValidator<V>>()

    override fun addValidator(validator: FieldValidator<V>) {
        validators.add(validator)
    }

    override fun getValidators(): List<FieldValidator<V>> = validators.toList()
    override fun clearValidators(): Unit = validators.clear()

}


internal class ValidateException(override val message: String) : Exception(message)