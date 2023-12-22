package io.github.windedge.viform.core


import kotlinx.coroutines.flow.*

public interface FormField<V : Any?> : ValidatorContainer<V> {
    public val name: String

    public val valueFlow: StateFlow<V>

    public val resultFlow: StateFlow<ValidateResult>

    public val currentValue: V

    public fun setValue(value: V, validate: Boolean = false)

    public fun setResult(result: ValidateResult)

    public fun showError(message: String)

    public fun validate(): Boolean
}

@Suppress("FunctionName")
public fun <V> FormField(name: String, initialValue: V): FormFieldImpl<V> {
    return FormFieldImpl(name, initialValue)
}

public class FormFieldImpl<V : Any?>(override val name: String, initialValue: V) : FormField<V> {
    private val _valueFlow: MutableStateFlow<V> = MutableStateFlow(initialValue)
    override val valueFlow: StateFlow<V> get() = _valueFlow.asStateFlow()

    private val _resultFlow: MutableStateFlow<ValidateResult> = MutableStateFlow(ValidateResult.None)
    override val resultFlow: StateFlow<ValidateResult> get() = _resultFlow.asStateFlow()

    override val currentValue: V get() = valueFlow.value

    private val validators = mutableListOf<FieldValidator<V>>()

    override fun addValidator(validator: FieldValidator<V>) {
        validators.add(validator)
    }

    override fun clearValidators(): Unit = validators.clear()

    override fun getValidators(): List<FieldValidator<V>> = validators.toList()

    override fun setValue(value: V, validate: Boolean) {
        _valueFlow.value = value
        _resultFlow.value = ValidateResult.None

        if (validate) validate()
    }

    public override fun setResult(result: ValidateResult) {
        _resultFlow.value = result
    }

    override fun showError(message: String) {
        setResult(ValidateResult.Failure(message))
    }

    override fun validate(): Boolean =
        validators.validateAndGet(currentValue).also { _resultFlow.value = it }.isSuccess
}
