package io.github.windedge.viform.core


interface FieldValidator<V> {
    fun validate(input: V): ValidateResult
}

fun <V> Custom(validation: (V) -> Boolean, errorMessage: String? = null): Custom<V> {
    val block = { input: V ->
        val success = validation(input)
        val result = if (success) {
            ValidateResult.Success
        } else {
            ValidateResult.Failure("Validation failed, value: $input")
        }
        result
    }
    return Custom(block, errorMessage)
}

class Custom<V>(val validation: (V) -> ValidateResult, private val errorMessage: String? = null) : FieldValidator<V> {
    override fun validate(input: V): ValidateResult {
        val validateResult = validation(input)
        if (validateResult.isOk()) {
            return validateResult
        }
        if (errorMessage != null) {
            return ValidateResult.Failure(errorMessage)
        }
        return validateResult
    }
}

class Nullable<V> : FieldValidator<V> {
    override fun validate(input: V): ValidateResult {
        error("Nulable shouldn't be invoked!")
    }
}

class Required<V> : FieldValidator<V> {
    override fun validate(input: V): ValidateResult {
        if (input == null) {
            return ValidateResult.Failure("Value can't be null")
        }
        if (input is String && input.isEmpty()) {
            return ValidateResult.Failure("Value can't be empty")
        }
        return ValidateResult.Success
    }
}

class Or<V>(private val left: FieldValidator<V>, private val right: FieldValidator<V>) : FieldValidator<V> {
    override fun validate(input: V): ValidateResult {
        val result1 = left.validate(input)
        val result2 = right.validate(input)

        if (result1.isSuccess() && result2.isSuccess()) {
            return ValidateResult.Success
        }

        if (result1.isFailed()) return result1
        if (result2.isFailed()) return result2

        return ValidateResult.Success
    }
}

class Numeric : FieldValidator<String> {
    override fun validate(input: String): ValidateResult {
        if (input.isNumeric()) {
            return ValidateResult.Success
        }
        return ValidateResult.Failure("Value is not a number")
    }
}

class StringBetween(val minValue: Int, val maxValue: Int) : FieldValidator<String> {
    override fun validate(input: String): ValidateResult {
        val value = input.toDoubleOrNull() ?: return ValidateResult.Failure("Value is not a number")
        if (value >= minValue && value <= maxValue) {
            return ValidateResult.Success
        }
        return ValidateResult.Failure("Value should be between $minValue and $maxValue")
    }
}

class IntBetween(val minValue: Int, val maxValue: Int) : FieldValidator<Int> {
    override fun validate(input: Int): ValidateResult {
        if (input in minValue..maxValue) {
            return ValidateResult.Success
        }
        return ValidateResult.Failure("Value should be between $minValue and $maxValue")
    }
}

class DoubleBetween(val minValue: Double, val maxValue: Double) : FieldValidator<Double> {
    override fun validate(input: Double): ValidateResult {
        if (input in minValue..maxValue) {
            return ValidateResult.Success
        }
        return ValidateResult.Failure("Value should be between $minValue and $maxValue")
    }
}
