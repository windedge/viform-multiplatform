package io.github.windedge.viform.core


import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

public interface FormHost<T : Any> {

    public val form: Form<T>

    public val stateFlow: StateFlow<T>

    public val currentState: T

    public fun submit(formData: T, validate: Boolean = true): Boolean

    public fun validate(): Boolean

    public fun pop(): T

    public fun reset()
}

public fun <T : Any> FormHost<T>.form(build: FormBuilder<T>.() -> Unit): Form<T> {
    val form = FormImpl(currentState)
    val builder = FormBuilder(form)
    builder.build()
    return form
}

public class SimpleFormHost<T : Any>(override val form: Form<T>) : FormHost<T> {

    private val _stateFlow: MutableStateFlow<T> = MutableStateFlow(form.pop())

    override val stateFlow: StateFlow<T> get() = _stateFlow.asStateFlow()

    override val currentState: T get() = _stateFlow.value

    override fun submit(formData: T, validate: Boolean): Boolean {
        _stateFlow.value = formData
        return form.submit(formData, validate)
    }

    override fun validate(): Boolean {
        return form.validate()
    }

    override fun pop(): T {
        val newState = form.pop()
        _stateFlow.value = newState
        return newState
    }

    override fun reset() {
        form.reset()
        _stateFlow.value = form.pop()
    }

}