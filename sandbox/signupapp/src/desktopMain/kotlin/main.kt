package local.sandbox.signupapp

import androidx.compose.material.MaterialTheme
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState


fun main() = application {
    val winState = rememberWindowState(width = 800.dp, height = 700.dp)
    Window(onCloseRequest = ::exitApplication, winState, title = "Sign Up") {
        MaterialTheme {
            SignupApp()
        }
    }
}

