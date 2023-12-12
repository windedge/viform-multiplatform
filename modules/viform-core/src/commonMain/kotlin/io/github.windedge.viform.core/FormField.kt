package io.github.windedge.viform.core


import kotlinx.coroutines.flow.*
import kotlin.reflect.KProperty0
import kotlin.reflect.KProperty1

public interface FormField<V : Any?> {
    public val value: V

    public val name: String

    public val valueFlow: StateFlow<V>

    public val resultFlow: StateFlow<ValidateResult>

    public fun addValidator(validator: FieldValidator<V>)

    public fun clearValidators()

    public fun setValue(value: V, validate: Boolean = false)

    public fun setResult(validateResult: ValidateResult)

    public fun validate(): Boolean
}

public fun <V : Any?> FormField<V>.addValidator(builder: () -> FieldValidator<V>): FormField<V> {
    val validator = builder()
    addValidator(validator)
    return this
}

public fun <T : Any, V> Form<T>.registerField(
    property: KProperty1<T, V>,
    build: FormField<V>.() -> Unit
): FormField<V> {
    val field = registerField(property)
    field.build()
    return field
}

public fun <T : Any, V> Form<T>.registerField(
    property: KProperty0<V>,
    build: FormField<V>.() -> Unit
): FormField<V> {
    val field = registerField(property)
    field.build()
    return field
}

public fun <V> FormField(name: String, initialValue: V): FormFieldImpl<V> {
    return FormFieldImpl(name, initialValue)
}

public class FormFieldImpl<V : Any?>(override val name: String, initialValue: V) : FormField<V> {

    private val _valueFlow: MutableStateFlow<V> = MutableStateFlow(initialValue)
    override val valueFlow: StateFlow<V> get() = _valueFlow.asStateFlow()

    private val _resultFlow: MutableStateFlow<ValidateResult> = MutableStateFlow(ValidateResult.None)
    override val resultFlow: StateFlow<ValidateResult> get() = _resultFlow.asStateFlow()
    override val value: V get() = valueFlow.value

    private val validators = mutableListOf<FieldValidator<V>>()

    override fun addValidator(validator: FieldValidator<V>) {
        validators.add(validator)
    }

    override fun clearValidators() {
        validators.clear()
    }

    override fun setValue(value: V, validate: Boolean) {
        _valueFlow.value = value
        _resultFlow.value = ValidateResult.None

        if (validate) {
            validate()
        }
    }

    override fun setResult(validateResult: ValidateResult) {
        _resultFlow.value = validateResult
    }

    override fun validate(): Boolean {
        val value = value
        var results = validators.map { it.preview(value) }
        val noise = results.find { it != ValidateResult.None }
        if (noise != null) {
            _resultFlow.value = noise
            return noise.isRealSuccess
        }

        results = validators.map { it.validate(value) }
        val isOk = results.all { it.isSuccess }
        return if (isOk) {
            _resultFlow.value = ValidateResult.Success
            true
        } else {
            _resultFlow.value = results.first { it is ValidateResult.Failure }
            false
        }
    }
}
