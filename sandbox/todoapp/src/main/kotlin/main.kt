@file:Suppress("FunctionName")

package local.sandbox.todoapp

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.onClick
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.windedge.copybuilder.KopyBuilder
import io.github.windedge.viform.compose.use
import io.github.windedge.viform.core.Form

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Todo") {
        MaterialTheme {
            TodoApp()
        }
    }
}

data class Todo(val text: String, val done: Boolean = false)

@KopyBuilder
data class TodoList(val todos: List<Todo> = emptyList())

@Preview
@Composable
fun TodoApp() {
    val form = Form(TodoList())
    form.use {
        field(it::todos) {
            Column(
                modifier = Modifier.fillMaxSize().padding(top = 50.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val todos: List<Todo> = this@field.value

                var text by remember { mutableStateOf("") }
                TodoInput(text, onTextChange = { text = it }, onAdd = {
                    if (text.isNotBlank()) {
                        setValue(todos + Todo(text.trim()), validate = true)
                        text = ""
                    }
                })

                TodoItems(todos, onItemChange = { isDone, itemIdx ->
                    val newTodos = todos.mapIndexed { index, todo ->
                        if (index == itemIdx) todo.copy(done = isDone) else todo
                    }
                    setValue(newTodos, validate = true)
                })
            }
        }
    }

}

@Composable
private fun TodoInput(text: String, onTextChange: (String) -> Unit, onAdd: () -> Unit) {
    Row(modifier = Modifier.width(400.dp), verticalAlignment = Alignment.CenterVertically) {
        TextField(
            text,
            onValueChange = onTextChange,
            singleLine = true,
            placeholder = { Text("What needs to be done?") },
            modifier = Modifier.weight(1f).onPreviewKeyEvent {
                if (it.type == KeyEventType.KeyDown && it.key == Key.Enter) {
                    onAdd()
                    return@onPreviewKeyEvent true
                }
                false
            },
        )
        Spacer(modifier = Modifier.width(10.dp))
        IconButton(onClick = onAdd) {
            Icon(Icons.Filled.Add, contentDescription = null)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TodoItems(todos: List<Todo>, onItemChange: (Boolean, Int) -> Unit) {
    LazyColumn(modifier = Modifier.padding(top = 20.dp).width(400.dp)) {
        this.items(todos.size) { idx ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                val current = todos[idx]
                Checkbox(current.done, onCheckedChange = { onItemChange(it, idx) })
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    current.text,
                    style = if (current.done) {
                        LocalTextStyle.current.copy(textDecoration = TextDecoration.LineThrough)
                    } else {
                        LocalTextStyle.current
                    },
                    modifier = Modifier.padding(vertical = 5.dp).focusable(false).onClick {
                        onItemChange(!current.done, idx)
                    }
                )
            }
            Divider()
        }
    }
}
