package io.github.windedge.viform.core


import kotlinx.coroutines.flow.StateFlow
import kotlin.reflect.KProperty0

interface FormHost<T : Cloneable<T>> {

    val form: Form<T>

    val stateFlow: StateFlow<T>

    val currentState: T

    fun submit(formData: T)

    fun <V : Any> submitField(property: KProperty0<V>, value: V)
}

fun <T : Cloneable<T>> FormHost<T>.form(build: FormBuilder<T>.() -> Unit): Form<T> {
    val form = FormImpl(currentState)
    val builder = FormBuilderImpl(form)
    builder.build()
    return form
}
