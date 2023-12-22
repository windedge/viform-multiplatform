package io.github.windedge.viform.core

public fun <V : Any?> ValidatorContainer<V>.addValidator(create: () -> FieldValidator<V>): ValidatorContainer<V> {
    val validator = create()
    addValidator(validator)
    return this
}

public fun <V> ValidatorContainer<V>.nullable(): ValidatorContainer<V & Any> {
    this.addValidator(Nullable())
    @Suppress("UNCHECKED_CAST")
    return this as ValidatorContainer<V & Any>
}

public fun <V> ValidatorContainer<V>.nullable(constrains: ValidatorContainer<V & Any>.() -> ValidatorContainer<V & Any>) {
    this.addValidator(Nullable())
    @Suppress("UNCHECKED_CAST")
    (this as ValidatorContainer<V & Any>).constrains()
}

public fun <V> ValidatorContainer<V>.not(
    errorMessage: String? = null,
    build: () -> FieldValidator<V>
): ValidatorContainer<V> =
    addValidator { Not(build(), errorMessage) }

public fun <V> ValidatorContainer<V>.anyOf(
    errorMessage: String? = null,
    vararg validators: FieldValidator<V>
): ValidatorContainer<V> =
    addValidator { AnyOf(*validators, errorMessage = errorMessage) }

public fun <V> ValidatorContainer<V>.allOf(
    errorMessage: String? = null,
    validators: Array<FieldValidator<V>>
): ValidatorContainer<V> =
    addValidator { AllOf(*validators, errorMessage = errorMessage) }

public fun <V> ValidatorContainer<V>.noneOf(
    errorMessage: String? = null,
    vararg validators: FieldValidator<V>
): ValidatorContainer<V> =
    addValidator { NoneOf(*validators, errorMessage = errorMessage) }

public fun <V> ValidatorContainer<V>.custom(
    errorMessage: String? = null,
    validation: (V) -> Boolean
): ValidatorContainer<V> =
    addValidator { Custom(validation, errorMessage) }

public fun <V> ValidatorContainer<V>.required(errorMessage: String? = null): ValidatorContainer<V> =
    addValidator { Required(errorMessage) }

public fun <V> ValidatorContainer<V>.equals(value: V, errorMessage: String? = null): ValidatorContainer<V> =
    addValidator { Equals(value, errorMessage) }

public fun ValidatorContainer<String>.isNumeric(errorMessage: String? = null): ValidatorContainer<String> =
    addValidator { Numeric(errorMessage) }

public fun ValidatorContainer<String>.isInt(errorMessage: String? = null): ValidatorContainer<String> =
    addValidator { Integer(errorMessage) }

public fun ValidatorContainer<String>.isFloat(errorMessage: String? = null): ValidatorContainer<String> =
    addValidator { Integer(errorMessage) }

public fun ValidatorContainer<Boolean>.isChecked(errorMessage: String? = null): ValidatorContainer<Boolean> =
    addValidator { Checked(errorMessage) }

public fun ValidatorContainer<String?>.optional(): ValidatorContainer<String> {
    addValidator(Optional())
    @Suppress("UNCHECKED_CAST")
    return this as ValidatorContainer<String>
}

public fun ValidatorContainer<String?>.optional(constrains: ValidatorContainer<String>.() -> ValidatorContainer<String>): ValidatorContainer<String> {
    addValidator(Optional())
    @Suppress("UNCHECKED_CAST")
    return (this as ValidatorContainer<String>).constrains()
}

public fun ValidatorContainer<String>.isBlank(errorMessage: String? = null): ValidatorContainer<String> =
    addValidator { Blank(errorMessage) }

public fun ValidatorContainer<String>.isNotBlank(errorMessage: String? = null): ValidatorContainer<String> =
    addValidator { NotBlank(errorMessage) }

public fun ValidatorContainer<String>.isEmpty(errorMessage: String? = null): ValidatorContainer<String> =
    addValidator { Empty(errorMessage) }

public fun ValidatorContainer<String>.isNotEmpty(errorMessage: String? = null): ValidatorContainer<String> =
    addValidator { NotEmpty(errorMessage) }

public fun ValidatorContainer<String>.matchesRegex(
    regex: String,
    errorMessage: String? = null
): ValidatorContainer<String> =
    addValidator { MatchesRegex(regex, errorMessage) }

public fun ValidatorContainer<String>.isAlphaNumeric(errorMessage: String? = null): ValidatorContainer<String> =
    addValidator { AlphaNumeric(errorMessage) }

public fun ValidatorContainer<String>.isUrl(errorMessage: String? = null): ValidatorContainer<String> =
    addValidator { Url(errorMessage) }

public fun ValidatorContainer<String>.isEmail(errorMessage: String? = null): ValidatorContainer<String> =
    addValidator { Email(errorMessage) }

public fun ValidatorContainer<String>.minLength(length: Int, errorMessage: String? = null): ValidatorContainer<String> =
    addValidator { MinLength(length, errorMessage) }

public fun ValidatorContainer<String>.maxLength(length: Int, errorMessage: String? = null): ValidatorContainer<String> =
    addValidator { MaxLength(length, errorMessage) }

public fun ValidatorContainer<String>.lengthBetween(
    minLength: Int, maxLength: Int, errorMessage: String? = null
): ValidatorContainer<String> = addValidator { LengthBetween(minLength, maxLength, errorMessage) }

public fun <V : Number> ValidatorContainer<V>.greaterThan(value: V, errMsg: String? = null): ValidatorContainer<V> =
    addValidator { GreaterThan(value, errMsg) }

public fun <V : Number> ValidatorContainer<V>.greaterThanOrEquals(
    value: V, errMsg: String? = null
): ValidatorContainer<V> =
    addValidator { GreaterThanOrEquals(value, errMsg) }

public fun <V : Number> ValidatorContainer<V>.lesserThan(value: V, errMsg: String? = null): ValidatorContainer<V> =
    addValidator { LesserThan(value, errMsg) }

public fun <V : Number> ValidatorContainer<V>.lesserThanOrEquals(
    value: V, errMsg: String? = null
): ValidatorContainer<V> =
    addValidator { LesserThanOrEquals(value, errMsg) }
