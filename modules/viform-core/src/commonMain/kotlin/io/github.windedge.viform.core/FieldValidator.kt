package io.github.windedge.viform.core


public interface FieldValidator<V> {
    public fun preview(input: V): ValidateResult = ValidateResult.None
    public fun validate(input: V): ValidateResult = ValidateResult.None
    public val errorMessage: String?
}

public fun <V> List<FieldValidator<V>>.validateAndGet(input: V): ValidateResult {
    this.map { it.preview(input) }.find { it != ValidateResult.None }?.let { return it }
    return this.map { it.validate(input) }.find { it.isError } ?: ValidateResult.Success
}

public class Nullable<V>(override val errorMessage: String? = null) : FieldValidator<V> {
    override fun preview(input: V): ValidateResult {
        if (input == null) {
            return ValidateResult.Success
        }
        return ValidateResult.None
    }
}

public abstract class LogicalValidator<V> : FieldValidator<V> {
    public abstract fun validateFunc(input: V): ValidateResult

    override fun preview(input: V): ValidateResult = ValidateResult.None

    override fun validate(input: V): ValidateResult = validateFunc(input)
}

public class Not<V>(
    private val validator: FieldValidator<V>, override val errorMessage: String? = null
) : LogicalValidator<V>() {

    override fun validateFunc(input: V): ValidateResult {
        val result = validator.validate(input)
        if (result.isSuccess) {
            return ValidateResult.Failure(errorMessage ?: "Value: $input do not satisfy the negative validation")
        }
        return ValidateResult.Success
    }

}

public class AnyOf<V>(
    private vararg val validators: FieldValidator<V>, override val errorMessage: String? = null
) : LogicalValidator<V>() {
    override fun validateFunc(input: V): ValidateResult {
        val results = validators.map { it.validate(input) }
        val success = results.any { it.isSuccess }
        if (success) {
            return ValidateResult.Success
        }

        return results.first { it.isError }
    }
}

public class AllOf<V>(
    private vararg val validators: FieldValidator<V>, override val errorMessage: String? = null
) : LogicalValidator<V>() {
    override fun validateFunc(input: V): ValidateResult {
        val results = validators.map { it.validate(input) }
        val success = results.all { it.isSuccess }
        if (success) {
            return ValidateResult.Success
        }

        return results.first { it.isError }
    }
}

public class NoneOf<V>(
    private vararg val validators: FieldValidator<V>, override val errorMessage: String? = null
) : LogicalValidator<V>() {
    override fun validateFunc(input: V): ValidateResult {
        val results = validators.map { it.validate(input) }
        val success = results.all { !it.isSuccess }
        if (success) {
            return ValidateResult.Success
        }

        return ValidateResult.Failure(errorMessage ?: "Value: $input do not satisfy all the negative validations")
    }
}


public class Custom<V>(public val func: (V) -> Boolean, override val errorMessage: String? = null) : FieldValidator<V> {
    override fun validate(input: V): ValidateResult {
        val success = func(input)
        if (success) {
            return ValidateResult.Success
        }
        return ValidateResult.Failure(errorMessage ?: "Validation is failed")
    }
}

public class Required<V>(override val errorMessage: String? = null) : FieldValidator<V> {
    override fun validate(input: V): ValidateResult {
        if (input == null) {
            return ValidateResult.Failure("Value can't be null")
        }
        if (input is String && input.isEmpty()) {
            return ValidateResult.Failure(errorMessage ?: "Should't be empty")
        }
        return ValidateResult.Success
    }
}

public class Equals<V>(private val value: V, override val errorMessage: String? = null) : FieldValidator<V> {
    override fun validate(input: V): ValidateResult {
        if (input == value) {
            return ValidateResult.Success
        }
        return ValidateResult.Failure(errorMessage ?: "Should be equals to $value")
    }
}

public class Numeric(override val errorMessage: String? = null) : FieldValidator<String> {
    override fun validate(input: String): ValidateResult {
        if (input.isNumeric()) {
            return ValidateResult.Success
        }
        return ValidateResult.Failure(errorMessage ?: "Shoud be a number")
    }
}

public class Integer(override val errorMessage: String? = null) : FieldValidator<String> {
    override fun validate(input: String): ValidateResult {
        if (input.isInt()) {
            return ValidateResult.Success
        }
        return ValidateResult.Failure(errorMessage ?: "Shoud be a integer number")
    }
}

public class Float(override val errorMessage: String? = null) : FieldValidator<String> {
    override fun validate(input: String): ValidateResult {
        if (input.isFloat()) {
            return ValidateResult.Success
        }
        return ValidateResult.Failure(errorMessage ?: "Shoud be a float number")
    }
}


public class Checked(override val errorMessage: String? = null) : FieldValidator<Boolean> {
    override fun validate(input: Boolean): ValidateResult {
        if (input) {
            return ValidateResult.Success
        }
        return ValidateResult.Failure(errorMessage ?: "Should be checked")
    }
}

public class Optional<V : String?>(override val errorMessage: String? = null) : FieldValidator<V> {
    override fun preview(input: V): ValidateResult {
        if (input.isNullOrEmpty()) {
            return ValidateResult.Success
        }
        return ValidateResult.None
    }
}

public class Blank(override val errorMessage: String?) : FieldValidator<String> {
    override fun validate(input: String): ValidateResult {
        if (input.isBlank()) {
            return ValidateResult.Success
        }
        return ValidateResult.Failure(errorMessage ?: "Should be blank string")
    }
}

public class NotBlank(override val errorMessage: String?) : FieldValidator<String> {
    override fun validate(input: String): ValidateResult {
        if (input.isNotBlank()) {
            return ValidateResult.Success
        }
        return ValidateResult.Failure(errorMessage ?: "Should not be blank string")
    }
}

public class Empty(override val errorMessage: String?) : FieldValidator<String> {
    override fun validate(input: String): ValidateResult {
        if (input.isEmpty()) {
            return ValidateResult.Success
        }
        return ValidateResult.Failure(errorMessage ?: "Should be empty string")
    }
}

public class NotEmpty(override val errorMessage: String?) : FieldValidator<String> {
    override fun validate(input: String): ValidateResult {
        if (input.isNotEmpty()) {
            return ValidateResult.Success
        }
        return ValidateResult.Failure(errorMessage ?: "Should not be empty string")
    }
}

public class MatchesRegex(
    private val regex: Regex, override val errorMessage: String? = null
) : FieldValidator<String> {
    override fun validate(input: String): ValidateResult {
        if (input.matches(regex)) {
            return ValidateResult.Success
        }
        return ValidateResult.Failure(errorMessage ?: "`${input}` does not match the specified regular expression")
    }
}

public fun MatchesRegex(regex: String, errorMessage: String? = null): MatchesRegex {
    return MatchesRegex(Regex(regex), errorMessage)
}

public class AlphaNumeric(override val errorMessage: String? = null) : FieldValidator<String> {
    override fun validate(input: String): ValidateResult {
        if (input.isAlphaNumeric()) {
            return ValidateResult.Success
        }
        return ValidateResult.Failure(errorMessage ?: "Should be an alpha numeric string")
    }
}

public class Url(override val errorMessage: String? = null) : FieldValidator<String> {
    override fun validate(input: String): ValidateResult {
        if (input.isUrl()) {
            return ValidateResult.Success
        }
        return ValidateResult.Failure(errorMessage ?: "Should be an email")
    }
}

public class Email(override val errorMessage: String? = null) : FieldValidator<String> {
    override fun validate(input: String): ValidateResult {
        if (input.isEmail()) {
            return ValidateResult.Success
        }
        return ValidateResult.Failure(errorMessage ?: "Should be an email")
    }
}

public class MinLength(private val minLength: Int, override val errorMessage: String? = null) : FieldValidator<String> {
    override fun validate(input: String): ValidateResult {
        if (input.length < minLength) {
            return ValidateResult.Failure("Should be more than $minLength characters")
        }
        return ValidateResult.Success
    }
}

public class MaxLength(private val maxLength: Int, override val errorMessage: String? = null) : FieldValidator<String> {
    override fun validate(input: String): ValidateResult {
        if (input.length > maxLength) {
            return ValidateResult.Failure("Should be less than $maxLength characters")
        }
        return ValidateResult.Success
    }
}

public class LengthBetween(
    private val minLength: Int, private val maxLength: Int, override val errorMessage: String? = null
) : FieldValidator<String> {
    override fun validate(input: String): ValidateResult {
        if (input.length < minLength || input.length > maxLength) {
            return ValidateResult.Failure("Should be in $minLength - $maxLength characters")
        }
        return ValidateResult.Success
    }
}

public class GreaterThan<V : Number>(
    private val value: V, override val errorMessage: String? = null
) : FieldValidator<V> {
    override fun validate(input: V): ValidateResult {
        if (input.toDouble() > value.toDouble()) {
            return ValidateResult.Success
        }
        return ValidateResult.Failure(errorMessage ?: "Should be greater than $value")
    }
}

public class GreaterThanOrEquals<V : Number>(
    private val value: V, override val errorMessage: String? = null
) : FieldValidator<V> {
    override fun validate(input: V): ValidateResult {
        if (input.toDouble() >= value.toDouble()) {
            return ValidateResult.Success
        }
        return ValidateResult.Failure(errorMessage ?: "Should be greater than or equals to $value")
    }
}

public class LesserThan<V : Number>(
    private val value: V, override val errorMessage: String? = null
) : FieldValidator<V> {
    override fun validate(input: V): ValidateResult {
        if (input.toDouble() < value.toDouble()) {
            return ValidateResult.Success
        }
        return ValidateResult.Failure(errorMessage ?: "Should be lesser than $value")
    }
}

public class LesserThanOrEquals<V : Number>(
    private val value: V, override val errorMessage: String? = null
) : FieldValidator<V> {
    override fun validate(input: V): ValidateResult {
        if (input.toDouble() <= value.toDouble()) {
            return ValidateResult.Success
        }
        return ValidateResult.Failure(errorMessage ?: "Should be lesser than or equals to $value")
    }
}
