package io.github.windedge.viform.core


public fun String.isNumeric(): Boolean {
    return this.toDoubleOrNull()?.let { true } ?: false
}
