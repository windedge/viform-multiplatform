package local.sandbox.signupapp

import io.github.windedge.copybuilder.KopyBuilder
import io.github.windedge.viform.core.*

@KopyBuilder
data class Signup(
    val name: String = "",
    val email: String? = null,
    val age: Int? = null,
    val password: String = "",
    val confirmPassword: String = "",
    val accept: Boolean = false,
)
