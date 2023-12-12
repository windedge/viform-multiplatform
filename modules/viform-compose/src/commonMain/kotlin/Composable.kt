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
    public fun <V : Any?> field(
        property: KProperty0<V>,
        content: @Composable FieldScope<T, V>.() -> Unit
    ) {
        val formField = form.getField(property)

        val valueState = formField.valueFlow.collectAsState()
        val resultState = formField.resultFlow.collectAsState()

        val fieldScope: FieldScope<T, V> = FieldScope(valueState.value, resultState.value,
            onStateChanged = { value, validate ->
                formField.setValue(value, validate)
            },
            onValidate = { formField.validate() }
        )
        fieldScope.content()
    }

    public fun <V : Any?> field(property: KProperty0<V>): FormField<V> {
        return form.getField(property)
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
        val wrapperField = form.getField(DummyProperty(wrapperName, wrapperState.value))
        if (constraints != null) {
            remember(constraints) { wrapperField.constraints() } // only add once
        }

        LaunchedEffect(this) {
            valueFlow.collectLatest { wrapperState.value = wrap(it) }
        }

        LaunchedEffect(this.resultFlow, wrapperField.resultFlow) {
            combine(resultFlow, wrapperField.resultFlow) { a, b -> arrayOf(a, b) }.collectLatest { results ->
                wrapperResultState.value = results.firstOrNull { it.isFailure } ?: results.first()
            }
        }

        val wrapperFieldScope = FieldScope<T, R>(wrapperState.value, wrapperResultState.value,
            onStateChanged = { value, validate ->
                wrapperState.value = value
                wrapperField.setValue(value, validate)
                this.setValue(unwrap(value), validate)
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
    public val value: V,
    public val result: ValidateResult,
    private val onStateChanged: (V, Boolean) -> Unit,
    private val onValidate: () -> Boolean,
) {
    public fun setValue(value: V, validate: Boolean = false) {
        onStateChanged(value, validate)
    }

    public fun validate(): Boolean {
        return onValidate()
    }

    @OptIn(FlowPreview::class)
    @Composable
    public fun watchLazily(debouncedTimeoutMills: Long = 800, block: (V) -> Unit) {
        LaunchedEffect(value) {
            snapshotFlow { value }.distinctUntilChanged().debounce(debouncedTimeoutMills).collectLatest {
                block(it)
            }
        }
    }
}
