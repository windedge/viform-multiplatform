package io.github.windedge.viform.core


public fun String.isNumeric(): Boolean {
    return this.toDoubleOrNull()?.let { true } ?: false
}

public fun String.isAlphaNumeric(): Boolean {
    return this.matches(Regex("^[a-zA-Z][a-zA-Z0-9]*$"))
}

