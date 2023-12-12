package io.github.windedge.viform.core


public fun <V> FormField<V>.nullable(constrains: (FormField<V & Any>.() -> FormField<V & Any>)? = null): FormField<V & Any> {
    this.addValidator(Nullable())
    @Suppress("UNCHECKED_CAST")
    val self = this as FormField<V & Any>
    if (constrains == null) {
        return self
    }
    return self.constrains()
}

public fun <V> FormField<V>.not(errorMessage: String? = null, build: () -> FieldValidator<V>): FormField<V> =
    addValidator { Not(build(), errorMessage) }

public fun <V> FormField<V>.anyOf(errorMessage: String? = null, vararg validators: FieldValidator<V>): FormField<V> =
    addValidator { AnyOf(*validators, errorMessage = errorMessage) }

public fun <V> FormField<V>.allOf(errorMessage: String? = null, validators: Array<FieldValidator<V>>): FormField<V> =
    addValidator { AllOf(*validators, errorMessage = errorMessage) }

public fun <V> FormField<V>.noneOf(errorMessage: String? = null, vararg validators: FieldValidator<V>): FormField<V> =
    addValidator { NoneOf(*validators, errorMessage = errorMessage) }

public fun <V> FormField<V>.custom(errorMessage: String? = null, validation: (V) -> Boolean): FormField<V> =
    addValidator { Custom(validation, errorMessage) }

public fun <V> FormField<V>.required(errorMessage: String? = null): FormField<V> =
    addValidator { Required(errorMessage) }

public fun <V> FormField<V>.equals(value: V, errorMessage: String? = null): FormField<V> =
    addValidator { Equals(value, errorMessage) }

public fun FormField<String>.isNumeric(errorMessage: String? = null): FormField<String> =
    addValidator { Numeric(errorMessage) }

public fun FormField<Boolean>.isChecked(errorMessage: String? = null): FormField<Boolean> =
    addValidator { Checked(errorMessage) }

public fun FormField<String?>.optional(constrains: (FormField<String>.() -> FormField<String>)? = null): FormField<String> {
    addValidator(Optional())
    @Suppress("UNCHECKED_CAST")
    val self = this as FormField<String>
    if (constrains == null) {
        return self
    }
    return self.constrains()
}

public fun FormField<String>.isBlank(errorMessage: String? = null): FormField<String> =
    addValidator { Blank(errorMessage) }

public fun FormField<String>.isNotBlank(errorMessage: String? = null): FormField<String> =
    addValidator { NotBlank(errorMessage) }

public fun FormField<String>.isEmpty(errorMessage: String? = null): FormField<String> =
    addValidator { Empty(errorMessage) }

public fun FormField<String>.isNotEmpty(errorMessage: String? = null): FormField<String> =
    addValidator { NotEmpty(errorMessage) }

public fun FormField<String>.matchesRegex(regex: String, errorMessage: String? = null): FormField<String> =
    addValidator { MatchesRegex(regex, errorMessage) }

public fun FormField<String>.isAlphaNumeric(errorMessage: String? = null): FormField<String> =
    addValidator { AlphaNumeric(errorMessage) }

public fun FormField<String>.isEmail(errorMessage: String? = null): FormField<String> =
    addValidator { Email(errorMessage) }

public fun FormField<String>.minLength(length: Int, errorMessage: String? = null): FormField<String> =
    addValidator { MinLength(length, errorMessage) }

public fun FormField<String>.maxLength(length: Int, errorMessage: String? = null): FormField<String> =
    addValidator { MaxLength(length, errorMessage) }

public fun FormField<String>.lengthBetween(
    minLength: Int, maxLength: Int, errorMessage: String? = null
): FormField<String> = addValidator { LengthBetween(minLength, maxLength, errorMessage) }

public fun <V : Comparable<V>> FormField<V>.greaterThan(value: V, errorMessage: String? = null): FormField<V> =
    addValidator { GreaterThan(value, errorMessage) }

public fun <V : Comparable<V>> FormField<V>.greaterThanOrEquals(value: V, errorMessage: String? = null): FormField<V> =
    addValidator { GreaterThanOrEquals(value, errorMessage) }

public fun <V : Comparable<V>> FormField<V>.lesserThan(value: V, errorMessage: String? = null): FormField<V> =
    addValidator { LesserThan(value, errorMessage) }

public fun <V : Comparable<V>> FormField<V>.lesserThanOrEquals(value: V, errorMessage: String? = null): FormField<V> =
    addValidator { LesserThanOrEquals(value, errorMessage) }
