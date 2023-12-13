package io.github.windedge.viform.compose

import androidx.compose.runtime.*
import io.github.windedge.viform.core.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlin.reflect.*

@Composable
public inline fun <T : Any> FormHost<T>.useForm(content: @Composable FormScope<T>.(T) -> Unit) {
    val formScope = FormScope(this.form)
    val state = this.stateFlow.collectAsState()
    formScope.content(state.value)
}

@Composable
public inline fun <T : Any> Form<T>.use(content: @Composable FormScope<T>.(T) -> Unit) {
    val host = SimpleFormHost(this)
    host.useForm(content)
}

public class FormScope<T : Any>(private val form: Form<T>) {

    @Composable
    public fun <V : Any?> field(property: KProperty0<V>, content: @Composable FieldScope<T, V>.() -> Unit) {
        val formField = form.getOrRegisterField(property)

        val valueState = remember { mutableStateOf(formField.value) }
        val resultState = formField.resultFlow.collectAsState()

        // skip the update when the update is from self
        var skipUpdate by remember { mutableStateOf(false) }
        LaunchedEffect(formField) {
            formField.valueFlow.collect {
                if (skipUpdate) skipUpdate = false else valueState.value = it
            }
        }

        val fieldScope: FieldScope<T, V> = FieldScope(valueState.value, resultState.value, formField,
            onValueChanged = { value, validate ->
                valueState.value = value
                skipUpdate = true
                formField.update(value, validate)
            },
            onValidate = { formField.validate() }
        )
        fieldScope.content()
    }

    public fun <V : Any?> field(property: KProperty0<V>): FormField<V> {
        return form.getOrRegisterField(property)
    }

    @Composable
    public fun <V, R> FormField<V>.wrapAs(
        wrap: (V) -> R,
        unwrap: (R) -> V,
        constraints: (FormField<R>.() -> Unit)? = null,
        content: @Composable FieldScope<T, R>.() -> Unit
    ) {
        val wrapperState = remember { mutableStateOf(wrap(this.value)) }
        val wrapperResultState: MutableState<ValidateResult> = remember { mutableStateOf(ValidateResult.None) }

        val wrapperName = "_${name}Wrapped"
        val wrapperField = form.getOrRegisterField(DummyProperty(wrapperName, wrapperState.value))
        if (constraints != null) {
            remember(constraints) { wrapperField.constraints() } // only add once
        }

        // skip the update when the update is from wrapperState
        var skipUpdate by remember { mutableStateOf(false) }
        LaunchedEffect(this) {
            valueFlow.collect {
                if (skipUpdate) skipUpdate = false else wrapperState.value = wrap(it)
            }
        }

        LaunchedEffect(this.resultFlow, wrapperField.resultFlow) {
            combine(wrapperField.resultFlow, resultFlow) { a, b -> arrayOf(a, b) }.collectLatest { results ->
                wrapperResultState.value = results.find { it.isError } ?: results.first()
            }
        }

        val wrapperFieldScope = FieldScope<T, R>(wrapperState.value, wrapperResultState.value, wrapperField,
            onValueChanged = { value, validate ->
                wrapperState.value = value
                wrapperField.update(value, validate)
                skipUpdate = true
                this.update(unwrap(value), validate)
            },
            onValidate = {
                wrapperField.validate() && this.validate()
            }
        )

        wrapperFieldScope.content()
    }
}

@Suppress("unused", "MemberVisibilityCanBePrivate")
public class FieldScope<T : Any, V : Any?>(
    public val currentValue: V,
    private val result: ValidateResult,
    private val field: FormField<V>,
    private val onValueChanged: (V, Boolean) -> Unit,
    private val onValidate: () -> Boolean,
) {
    public val hasError: Boolean get() = result.isError
    public val errorMessage: String? = result.errorMessage

    public fun update(value: V, validate: Boolean = false) {
        onValueChanged(value, validate)
    }

    public fun validate(): Boolean {
        return onValidate()
    }

    @OptIn(FlowPreview::class)
    @Composable
    public fun watchLazily(debouncedTimeoutMills: Long = 800, block: (V) -> Unit) {
        LaunchedEffect(currentValue) {
            snapshotFlow { currentValue }.distinctUntilChanged().debounce(debouncedTimeoutMills).collectLatest {
                block(it)
            }
        }
    }
}
