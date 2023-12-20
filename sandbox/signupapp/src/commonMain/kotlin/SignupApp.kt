package local.sandbox.signupapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
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
import io.github.windedge.viform.compose.use
import io.github.windedge.viform.core.*

val schema = FormSchema.create {
    field(Signup::name) {
        required("User name is required.")
    }
    field(Signup::email).optional {
        isEmail()
    }
    field(Signup::age).nullable {
        greaterThan(0)
    }

    // chained style
    field(Signup::password).required().lengthBetween(8, 20)

    //custom rule
    field(Signup::confirmPassword).required().lengthBetween(8, 20)
        .custom("Passwords must be the same.") {
            it == field(Signup::password).currentValue
        }

    field(Signup::accept) {
        isChecked("Your must accept the terms of agreement.")
    }
}

@Composable
fun SignupApp() {
    val form = schema.buildForm(Signup())
    form.use {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp, vertical = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Surface(elevation = 5.dp) {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
                    field(it::name) {
                        TextInput("User Name: ", currentValue, hasError, errorMessage, onValueChange = {
                            setValue(it, validate = true)
                        })
                    }
                    field(it::email) {
                        watchLazily { validate() }
                        TextInput("Email:", currentValue ?: "", hasError, errorMessage) {
                            setValue(it)
                        }
                    }
                    field(it::age).wrapAs(
                        wrap = { it?.toString() },
                        unwrap = { it?.toIntOrNull() },
                        constraints = { optional { isInt() } },
                    ) {
                        TextInput("Age:", currentValue ?: "", hasError, errorMessage) {
                            setValue(it)
                        }
                    }
                    field(it::password) {
                        watchLazily { if (it.isNotEmpty()) validate() }
                        TextInput("Password:", currentValue, hasError, errorMessage, true) {
                            setValue(it)
                        }
                    }

                    field(it::confirmPassword) {
                        watchLazily { if (it.isNotEmpty()) validate() }
                        TextInput("Confirm Password:", currentValue, hasError, errorMessage, true) {
                            setValue(it)
                        }
                    }

                }
            }

            field(it::accept) {
                InputLayout(hasError, errorMessage) {
                    Checkbox(currentValue, onCheckedChange = { setValue(it) })
                    Text("I accept the terms of the agreement",
                        modifier = Modifier.clickable { setValue(!currentValue) })
                }
            }

            Spacer(modifier = androidx.compose.ui.Modifier.height(10.dp))

            var signedUp by remember { mutableStateOf(false) }
            if (signedUp) {
                Button(onClick = { signedUp = false; form.reset() }) {
                    Text("Reset", color = Color.White)
                }
            } else {
                Button(onClick = {
                    signedUp = form.validate()


                    form.pop()

                }) {
                    Text("Sign up", color = Color.White)
                }
            }

            if (signedUp) {
                val result = form.pop()
                Spacer(modifier = androidx.compose.ui.Modifier.height(10.dp))
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
    Column(modifier = androidx.compose.ui.Modifier.padding(5.dp)) {
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