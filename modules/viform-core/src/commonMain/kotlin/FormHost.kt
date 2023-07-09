package io.github.windedge.viform.core


import kotlinx.coroutines.flow.StateFlow
import kotlin.reflect.KProperty0

public interface FormHost<T : Cloneable<T>> {

    public val form: Form<T>

    public val stateFlow: StateFlow<T>

    public val currentState: T

    public fun submit(formData: T)

    public fun <V : Any> submitField(property: KProperty0<V>, value: V)
}

public fun <T : Cloneable<T>> FormHost<T>.form(build: FormBuilder<T>.() -> Unit): Form<T> {
    val form = FormImpl(currentState)
    val builder = FormBuilderImpl(form)
    builder.build()
    return form
}
