package io.github.windedge.viform.core


fun String.isNumeric(): Boolean {
    return this.toDoubleOrNull()?.let { true } ?: false
}
