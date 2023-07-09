package io.github.windedge.viform.core


import kotlinx.coroutines.flow.MutableStateFlow

public sealed class ValidateResult {

    public object None : ValidateResult()

    public object Success : ValidateResult()

    public class Failure(public val message: String) : ValidateResult()

    public fun isSuccess(): Boolean {
        return this is Success
    }

    public fun isOk(): Boolean {
        return this is None || this is Success
    }

    public fun isFailed(): Boolean {
        return this is Failure
    }

    public val errorMessage: String
        get() {
            require(this is Failure) { "There is no failure, can't get error message" }
            return this.message
        }
}

public interface FormField<V : Any?> {
    public val value: V

    public val name: String

    public val valueFlow: MutableStateFlow<V>

    public val resultFlow: MutableStateFlow<ValidateResult>

    public fun addValidator(fieldValidator: FieldValidator<V>)

    public fun setValue(value: V, validate: Boolean = true)

    public fun validate(): Boolean
}


internal class FormFieldImpl<T, V : Any?>(override val name: String, initialValue: V) : FormField<V> {

    override val valueFlow: MutableStateFlow<V> = MutableStateFlow(initialValue)

    override val resultFlow: MutableStateFlow<ValidateResult> = MutableStateFlow(ValidateResult.None)

    override val value: V get() = valueFlow.value

    private val validators = mutableListOf<FieldValidator<V>>()

    override fun addValidator(fieldValidator: FieldValidator<V>) {
        validators.add(fieldValidator)
    }

    override fun setValue(value: V, validate: Boolean) {
        valueFlow.value = value
        resultFlow.value = ValidateResult.None

        if (validate) {
            validate()
        }
    }

    override fun validate(): Boolean {
        if (value == null) {
            return if (isNullable()) {
                resultFlow.value = ValidateResult.Success
                true
            } else {
                resultFlow.value = ValidateResult.Failure("Value can't be null, field: ${this.name}")
                false
            }
        }

        val results = validators.filterNot { it is Nullable }.map { it.validate(value) }
        val success = results.all { it.isSuccess() }
        return if (success) {
            resultFlow.value = ValidateResult.Success
            true
        } else {
            resultFlow.value = results.first { it is ValidateResult.Failure }
            false
        }
    }

    private fun isNullable(): Boolean {
        return validators.any { it is Nullable }
    }
}
