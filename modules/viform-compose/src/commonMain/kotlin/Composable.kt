package io.github.windedge.viform.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlin.reflect.KProperty0

@Composable
fun <T : Cloneable<T>> FormHost<T>.useForm(content: @Composable FormScope<T>.(T) -> Unit) {
    val formScope = FormScope(this)
    val state = this.stateFlow.collectAsState()
    formScope.content(state.value)
}

data class ValueAndResult<V>(val value: V, val result: ValidateResult)

class FormScope<T : Cloneable<T>>(private val formHost: FormHost<T>) {
    @Composable
    fun <V : Any> field(
        property: KProperty0<V>,
        content: @Composable FieldScope<T, V>.(ValueAndResult<V>) -> Unit
    ) {
        val formField = formHost.form.getFormField(property)
        val initialValue = property.get()
        val valueState = formField.valueFlow.collectAsState(initialValue)
        val resultState = formField.resultFlow.collectAsState()
        val fieldScope = FieldScope(formHost, property)

        val valueResult = ValueAndResult(valueState.value, resultState.value)
        fieldScope.content(valueResult)
    }
}

@Suppress("unused")
class FieldScope<T : Cloneable<T>, V : Any>(private val formHost: FormHost<T>, val property: KProperty0<V>) {
    val form get() = formHost.form

    @Suppress("MemberVisibilityCanBePrivate")
    val formField: FormField<V>
        get() = form.getFormField(property)

    val valueFlow: StateFlow<V>
        get() = formField.valueFlow

    val value: V get() = valueFlow.value

    @Suppress("MemberVisibilityCanBePrivate")
    val resultFlow: StateFlow<ValidateResult>
        get() = formField.resultFlow

    fun setValue(value: V, validate: Boolean = false) {
        formField.setValue(value, validate)
    }

    fun submitValue(value: V, validate: Boolean = true) {
        setValue(value, false)
        submitValue(validate)
    }

    fun submitValue(validate: Boolean = true) {
        if (validate()) {
            val pass = if (validate) validate() else true
            if (pass) {
                formHost.submitField(property, value)
            }
        }
    }

    fun validate(): Boolean {
        return formField.validate()
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun submitForm(formData: T) {
        formHost.submit(formData)
    }

    @OptIn(FlowPreview::class)
    @Composable
    fun watchLazily(debouncedTimeoutMills: Long = 300L, block: (V) -> Unit) {
        LaunchedEffect(formField.valueFlow) {
            formField.valueFlow.debounce(debouncedTimeoutMills).collect {
                block(it)
            }
        }
    }
}
