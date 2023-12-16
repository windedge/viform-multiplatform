package io.github.windedge.viform.compose

import androidx.compose.runtime.*
import io.github.windedge.viform.core.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlin.reflect.*

@Composable
public inline fun <T : Any> FormHost<T>.useForm(content: @Composable FormScope<T>.(T) -> Unit) {
    val state = this.stateFlow.collectAsState()
    FormScope(form).content(state.value)
}

@Composable
public inline fun <T : Any> Form<T>.use(content: @Composable FormScope<T>.(T) -> Unit) {
    DefaultFormHost(this).useForm(content)
}

public class FormScope<T : Any>(private val form: Form<T>) {

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
        val validatable = remember {
            SimpleValidatorContainer<R>().also { it.constraints() } // only run once
        }

        val wrappedFormField = this as? WrappedFormField<V> ?: remember {
            WrappedFormField(this).apply {
                form.replaceField(this)
            }
        }

        val handler = wrappedFormField.getOrRegisterHandler(
            wrappedType, validatable.getValidators(), wrap, unwrap, this.currentValue
        )

        val resultState = this.resultFlow.collectAsState(ValidateResult.None)
        val fieldScope: FieldScope<T, R> = with(handler) {
            FieldScope(handler.wrappedState.value, resultState.value, ::setWrappedSate, ::validate)
        }
        fieldScope.content()
    }

}

public class FieldScope<T : Any, V : Any?>(
    public val currentValue: V,
    private val result: ValidateResult,
    private val onValueChanged: (V, Boolean) -> Unit,
    private val onValidate: () -> Boolean,
) {
    public val hasError: Boolean get() = result.isError
    public val errorMessage: String? = result.errorMessage

    public fun setValue(value: V, validate: Boolean = false) {
        onValueChanged(value, validate)
    }

    public fun validate(): Boolean {
        return onValidate()
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

internal data class FieldChangeEvent<V>(
    val value: V,
    val validate: Boolean,
    val notifier: Any,
)

internal class WrappedFormField<V>(private val origin: FormField<V>) : FormField<V> by origin {
    private val handlers = mutableMapOf<Pair<*, *>, WrappedStateHandler<V, *>>()

    fun <R> getOrRegisterHandler(
        wrappedType: KType,
        wrappedValidators: List<FieldValidator<R>>,
        wrap: (V) -> R,
        unwrap: (R) -> V,
        initialvalue: V,
    ): WrappedStateHandler<V, R> {
        val key = Pair(name, wrappedType)
        @Suppress("UNCHECKED_CAST")
        return handlers.getOrPut(key) {
            WrappedStateHandler(this, wrappedType, wrappedValidators, wrap, unwrap, initialvalue)
        } as WrappedStateHandler<V, R>
    }

    fun notifyFieldChanged(event: FieldChangeEvent<V>) {
        if (event.notifier != this) {
            origin.setValue(event.value, event.validate)
        }
        handlers.values.forEach {
            it.handleFieldChanged(event)
        }
    }

    override fun setValue(value: V, validate: Boolean) {
        origin.setValue(value, false)
        notifyFieldChanged(FieldChangeEvent(value, false, this))

        if (validate) validate()
    }

    override fun validate(): Boolean {
        return origin.validate() && handlers.values.map { it.validate() }.all { it }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is WrappedFormField<*>) return false

        if (origin != other.origin) return false
        if (handlers != other.handlers) return false

        return true
    }

    override fun hashCode(): Int {
        var result = origin.hashCode()
        result = 31 * result + handlers.hashCode()
        return result
    }
}

internal class WrappedStateHandler<V, R>(
    private val owner: WrappedFormField<V>,
    private val wrappedType: KType,
    private val wrappedValidators: List<FieldValidator<R>>,
    private val wrap: (V) -> R,
    private val unwrap: (R) -> V,
    initialValue: V,
) {
    private val _wrappedState: MutableState<R> = mutableStateOf(wrap(initialValue))
    val wrappedState: State<R> get() = _wrappedState

    fun handleFieldChanged(event: FieldChangeEvent<V>) {
        if (event.notifier == this) return

        _wrappedState.value = wrap(event.value)
        if (event.validate) validate()
    }

    fun setWrappedSate(value: R, validate: Boolean = false) {
        _wrappedState.value = value

        val event = FieldChangeEvent(unwrap(value), validate, this)
        owner.notifyFieldChanged(event)
        if (validate) validate()
    }

    fun validate(): Boolean {
        return wrappedValidators.validateAndGet(_wrappedState.value).also { owner.setResult(it) }.isSuccess
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is WrappedStateHandler<*, *>) return false

        if (owner != other.owner) return false
        if (wrappedType != other.wrappedType) return false
        if (_wrappedState != other._wrappedState) return false
        if (wrappedValidators != other.wrappedValidators) return false
        if (wrap != other.wrap) return false
        if (unwrap != other.unwrap) return false

        return true
    }

    override fun hashCode(): Int {
        var result = wrappedType.hashCode()
        result = 31 * result + _wrappedState.hashCode()
        result = 31 * result + wrappedValidators.hashCode()
        result = 31 * result + wrap.hashCode()
        result = 31 * result + unwrap.hashCode()
        return result
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