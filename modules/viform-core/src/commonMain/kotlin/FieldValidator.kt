package io.github.windedge.viform.core


public interface FieldValidator<V> {
    public fun validate(input: V): ValidateResult
}

public fun <V> Custom(validation: (V) -> Boolean, errorMessage: String? = null): Custom<V> {
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

public class Custom<V>(public val validation: (V) -> ValidateResult, private val errorMessage: String? = null) : FieldValidator<V> {
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

public class Nullable<V> : FieldValidator<V> {
    override fun validate(input: V): ValidateResult {
        error("Nulable shouldn't be invoked!")
    }
}

public class Required<V> : FieldValidator<V> {
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

public class Or<V>(private val left: FieldValidator<V>, private val right: FieldValidator<V>) : FieldValidator<V> {
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

public class Numeric : FieldValidator<String> {
    override fun validate(input: String): ValidateResult {
        if (input.isNumeric()) {
            return ValidateResult.Success
        }
        return ValidateResult.Failure("Value is not a number")
    }
}

public class StringBetween(public val minValue: Int, public val maxValue: Int) : FieldValidator<String> {
    override fun validate(input: String): ValidateResult {
        val value = input.toDoubleOrNull() ?: return ValidateResult.Failure("Value is not a number")
        if (value >= minValue && value <= maxValue) {
            return ValidateResult.Success
        }
        return ValidateResult.Failure("Value should be between $minValue and $maxValue")
    }
}

public class IntBetween(public val minValue: Int, public val maxValue: Int) : FieldValidator<Int> {
    override fun validate(input: Int): ValidateResult {
        if (input in minValue..maxValue) {
            return ValidateResult.Success
        }
        return ValidateResult.Failure("Value should be between $minValue and $maxValue")
    }
}

public class DoubleBetween(public val minValue: Double, public val maxValue: Double) : FieldValidator<Double> {
    override fun validate(input: Double): ValidateResult {
        if (input in minValue..maxValue) {
            return ValidateResult.Success
        }
        return ValidateResult.Failure("Value should be between $minValue and $maxValue")
    }
}
