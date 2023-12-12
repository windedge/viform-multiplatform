package test

import io.github.windedge.copybuilder.KopyBuilder

@KopyBuilder
data class User(
    val name: String,
    val age: Int?,
)