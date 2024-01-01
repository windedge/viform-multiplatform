package io.github.windedge.viform.mvi

import io.github.windedge.viform.core.FormHost
import kotlinx.coroutines.flow.StateFlow
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.annotation.OrbitDsl
import org.orbitmvi.orbit.annotation.OrbitInternal
import org.orbitmvi.orbit.syntax.simple.SimpleContext
import org.orbitmvi.orbit.syntax.simple.SimpleSyntax
import org.orbitmvi.orbit.syntax.simple.intent


public abstract class FormContainerHost<STATE : Any, SIDE_EFFECT : Any>
    : ContainerHost<STATE, SIDE_EFFECT>, FormHost<STATE> {

    override val stateFlow: StateFlow<STATE> get() = container.stateFlow

    override val currentState: STATE get() = container.stateFlow.value

    override fun submit(formData: STATE, validate: Boolean) : Boolean {
        intent { reduce { formData } }
        return form.submit(formData, validate)
    }

    override fun validate(): Boolean = form.validate()

    override fun pop(): STATE {
        val state = form.pop(currentState)
        intent { reduce { state } }
        return state
    }

    override fun reset() {
        form.reset()
        intent { reduce { form.pop() } }
    }

    @OptIn(OrbitInternal::class)
    @OrbitDsl
    public suspend fun <SE : Any> SimpleSyntax<STATE, SE>.reduce(
        validate: Boolean = false,
        reducer: SimpleContext<STATE>.() -> STATE
    ) {
        containerContext.reduce { oldState ->
            val newState = SimpleContext(oldState).reducer()
            val success = this@FormContainerHost.form.submit(newState, validate)
            if (success) newState else oldState
        }
    }
}
