package io.github.windedge.viform.compose

import androidx.compose.runtime.*
import io.github.windedge.viform.core.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlin.reflect.*

@Composable
public fun <T : Any> FormHost<T>.useForm(content: @Composable FormScope<T>.(T) -> Unit) {
    val state = this.stateFlow.collectAsState()
    FormScope(form).content(state.value)
}

@Composable
public fun <T : Any> Form<T>.use(content: @Composable FormScope<T>.(T) -> Unit) {
    val formHost = DefaultFormHost(this)
    val state = formHost.stateFlow.collectAsState()
    FormScope(this).content(state.value)
}

public class FormScope<T : Any>(public val form: Form<T>) {

    @Composable
    public fun <V : Any?> field(property: KProperty0<V>): FormField<V> {
        return form.getOrRegisterField(property)
    }

    @Composable
    public inline fun <reified V> field(
        property: KProperty0<V>,
        noinline content: @Composable FieldScope<T, V>.() -> Unit
    ) {
        val wrappedType = typeOf<V>()
        field(property).wrapAsType(wrappedType, { it }, { it }, content = content)
    }

    @Composable
    public inline fun <V, reified R> FormField<V>.wrapAs(
        noinline wrap: (V) -> R,
        noinline unwrap: (R) -> V,
        noinline constraints: ValidatorContainer<R>.() -> Unit = {},
        noinline content: @Composable FieldScope<T, R>.() -> Unit,
    ) {
        val wrappedType = typeOf<R>()
        this.wrapAsType(wrappedType, wrap, unwrap, constraints, content)
    }

    @Composable
    public fun <V, R> FormField<V>.wrapAsType(
        wrappedType: KType,
        wrap: (V) -> R,
        unwrap: (R) -> V,
        constraints: ValidatorContainer<R>.() -> Unit = {},
        content: @Composable() (FieldScope<T, R>.() -> Unit),
    ) {
        @Suppress("UNCHECKED_CAST")
        val wrappedFormField = this as? WrappedFormField<V, R> ?: remember(this.name) {
            WrappedFormField(this, wrappedType, wrap, unwrap).apply {
                form.replaceField(this)
            }
        }
        if (wrappedFormField.wrappedType != wrappedType) {
            error("Can't wrap as another type!")
        }

        remember { wrappedFormField.applyConstraints(constraints) }
        DisposableEffect(name) {
            onDispose { form.replaceField(wrappedFormField.origin) }
        }

        val resultState = this.resultFlow.collectAsState(ValidateResult.None)
        val fieldScope: FieldScope<T, R> = with(wrappedFormField) {
            FieldScope(wrappedState.value, resultState.value, ::setWrappedSate, ::validate, onShowError = ::showError)
        }
        fieldScope.content()
    }

}

public class FieldScope<T : Any, V : Any?>(
    public val currentValue: V,
    private val result: ValidateResult,
    private val onValueChanged: (V, Boolean) -> Unit,
    private val onValidate: () -> Boolean,
    private val onShowError: (String) -> Unit,
) {
    public val hasError: Boolean get() = result.isError
    public val errorMessage: String? = result.errorMessage

    public fun setValue(value: V, validate: Boolean = false) {
        onValueChanged(value, validate)
    }

    public fun setValue(value: V) {
        onValueChanged(value, false)
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
    val wrap: (V) -> R,
    val unwrap: (R) -> V
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

    fun showError(errorMessage: String) {
        setResult(ValidateResult.Failure(errorMessage))
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