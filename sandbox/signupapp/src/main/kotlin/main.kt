package local.sandbox.signupapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Checkbox
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.github.windedge.viform.compose.use
import io.github.windedge.viform.core.*


fun main() = application {
    val form = Form(Signup()).apply {
        registerField(Signup::name) {
            required("User name is required.")
            isAlphaNumeric()
        }
        registerField(Signup::email).optional {
            isEmail()
        }
        registerField(Signup::age).nullable {
            greaterThan(0)
        }
        registerField(Signup::password).required().lengthBetween(8, 20)    // chained style
        registerField(Signup::confirmPassword) {
            required()
            lengthBetween(8, 20)
            custom("Passwords must be the same.") {
                val password = this@apply.getField(Signup::password).value
                it == password
            }
        }
        registerField(Signup::accept) {
            isChecked("Your must accept the terms of agreement.")
        }
    }


    val winState = rememberWindowState(width = 800.dp, height = 700.dp)
    Window(onCloseRequest = ::exitApplication, winState, title = "Sign Up") {
        MaterialTheme {
            Box(modifier = Modifier.fillMaxSize(), Alignment.Center) {
                SignupApp(form)
            }
        }
    }
}

@Composable
fun SignupApp(form: Form<Signup>) {
    form.use {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(elevation = 5.dp) {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
                    field(it::name) {
                        TextInput("User Name: ", value, result.isFailure, result.errorMessage) {
                            setValue(it, validate = true)
                        }
                    }
                    field(it::email) {
                        watchLazily(800) { validate() }
                        TextInput("Email:", value ?: "", result.isFailure, result.errorMessage) {
                            setValue(it)
                        }
                    }
                    field(it::age).wrapAs(
                        wrap = { it?.toString() },
                        unwrap = { it?.toIntOrNull() },
                        constraints = { optional { isNumeric() } }
                    ) {
//                        watchLazily(800) { validate() }
                        TextInput("Age:", value ?: "", result.isFailure, result.errorMessage) {
                            setValue(it)
                        }
                    }
                    field(it::password) {
                        watchLazily(800) { if (it.isNotEmpty()) validate() }
                        TextInput("Password:", value, result.isFailure, result.errorMessage, true) {
                            setValue(it)
                        }
                    }

                    field(it::confirmPassword) {
                        watchLazily(800) { if (it.isNotEmpty()) validate() }
                        TextInput("Confirm Password:", value, result.isFailure, result.errorMessage, true) {
                            setValue(it)
                        }
                    }

                }
            }

            field(it::accept) {
                InputLayout(result.isFailure, result.errorMessage) {
                    Checkbox(value, onCheckedChange = { setValue(it) })
                    Text("I accept the terms of the agreement", modifier = Modifier.clickable { setValue(!value) })
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            var signedUp by remember { mutableStateOf(false) }
            if (signedUp) {
                Button(onClick = { signedUp = false; form.reset() }) {
                    Text("Reset", color = Color.White)
                }
            } else {
                Button(onClick = { if (form.validate()) signedUp = true }) {
                    Text("Sign up", color = Color.White)
                }
            }

            if (signedUp) {
                val result = form.pop()
                Spacer(modifier = Modifier.height(10.dp))
                Text("Your signup's information is: \n${result}")
            }

        }

    }
}

@Composable
fun TextInput(
    label: String,
    text: String,
    isError: Boolean,
    errorMessage: String? = "",
    isPassword: Boolean = false,
    onValueChange: (String) -> Unit,
) {
    InputLayout(isError = isError, errorMessage = errorMessage) {
        OutlinedTextField(
            text,
            label = { Text(label) },
            onValueChange = onValueChange,
            singleLine = true,
            modifier = Modifier.moveFocusOnTab(),
            isError = isError,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None
        )
    }
}

@Composable
fun InputLayout(isError: Boolean, errorMessage: String? = "", content: @Composable RowScope.() -> Unit) {
    Column(modifier = Modifier.padding(5.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            this.content()
        }
        if (isError) {
            Text(
                errorMessage ?: "",
                color = Color.Red,
                modifier = Modifier.padding(top = 5.dp).align(Alignment.End)
            )
        }
    }
}

fun Modifier.moveFocusOnTab() = composed {
    val focusManager = LocalFocusManager.current
    onPreviewKeyEvent {
        if (it.type == KeyEventType.KeyDown && it.key == Key.Tab) {
            focusManager.moveFocus(
                if (it.isShiftPressed) FocusDirection.Previous else FocusDirection.Next
            )
            true
        } else {
            false
        }
    }
}