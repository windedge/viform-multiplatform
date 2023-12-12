package local.sandbox.signupapp

import io.github.windedge.copybuilder.KopyBuilder

@KopyBuilder
data class Signup(
    val name: String = "",
    val email: String? = null,
    val age: Int? = null,
    val password: String = "",
    val confirmPassword: String = "",
    val accept: Boolean = false,
)