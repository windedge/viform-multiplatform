@file:Suppress("unused")

package io.github.windedge.viform.core


internal fun String.isNumeric(): Boolean {
    return this.matches(Regex("[+-]?\\d+(\\.\\d+)?"))
}

internal fun String.isInt(): Boolean {
    return this.matches(Regex("[+-]?\\d+"))
}

internal fun String.isFloat(): Boolean {
    return this.matches(Regex("[+-]?\\d+\\.\\d+"))
}

internal fun String.isEmail(): Boolean {
    return this.matches(Regex("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&’*+/=?`{|}~^-]+)*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]{2,})*$"))
}

internal fun String.isUrl(): Boolean {
    return this.matches(Regex("^(http|https)://[^\\s$.?#].[^\\s]*$"))
}

internal fun String.isIPv4(): Boolean {
    return this.matches(Regex("^(?:(?:25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.){3}(?:25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\$"))
}

internal fun String.isIPv6(): Boolean {
    return this.matches(Regex("^(?:[A-F0-9]{1,4}:){7}[A-F0-9]{1,4}\$"))
}

internal fun String.isHexColor(): Boolean {
    return this.matches(Regex("^#?([a-fA-F0-9]{6}|[a-fA-F0-9]{3})\$"))
}

internal fun String.isHexadecimal(): Boolean {
    return this.matches(Regex("^[0-9a-fA-F]+\$"))
}

internal fun String.isCreditCard(): Boolean {
    return this.matches(Regex("^((4\\d{3})|(5[1-5]\\d{2})|(6011)|(7\\d{3}))-?\\d{4}-?\\d{4}-?\\d{4}|3[4,7]\\d{13}\$"))
}

internal fun String.isAlphaNumeric(): Boolean {
    return this.matches(Regex("^[a-zA-Z][a-zA-Z0-9]*$"))
}

