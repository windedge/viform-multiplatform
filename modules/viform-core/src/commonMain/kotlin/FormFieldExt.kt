package io.github.windedge.viform.core


fun <V> FormField<V>.nullable(): FormField<V> {
    addValidator(Nullable())
    return this
}

fun <V> FormField<V>.custom(validation: (V) -> ValidateResult): FormField<V> {
    addValidator(Custom(validation))
    return this
}

fun <V> FormField<V>.custom(validation: (V) -> Boolean, errorMessage: String? = null): FormField<V> {
    addValidator(Custom(validation, errorMessage))
    return this
}

fun <V> FormField<V>.required(): FormField<V> {
    addValidator(Required())
    return this
}

fun FormField<String>.isNumeric(): FormField<String> {
    addValidator(Numeric())
    return this
}

fun FormField<String>.between(minValue: Int, maxValue: Int): FormField<String> {
    addValidator(StringBetween(minValue, maxValue))
    return this
}
