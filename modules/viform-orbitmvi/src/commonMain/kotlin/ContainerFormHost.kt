package io.github.windedge.viform.mvi

import io.github.windedge.copybuilder.CopyBuilderFactory
import io.github.windedge.viform.core.FormHost
import kotlinx.coroutines.flow.StateFlow
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.annotation.OrbitDsl
import org.orbitmvi.orbit.annotation.OrbitInternal
import org.orbitmvi.orbit.syntax.simple.SimpleContext
import org.orbitmvi.orbit.syntax.simple.SimpleSyntax
import org.orbitmvi.orbit.syntax.simple.intent
import kotlin.reflect.KProperty0


public abstract class ContainerFormHost<STATE : Any, SIDE_EFFECT : Any>
    : ContainerHost<STATE, SIDE_EFFECT>, FormHost<STATE> {

    override val stateFlow: StateFlow<STATE> get() = container.stateFlow

    override val currentState: STATE get() = container.stateFlow.value

    @Suppress("MemberVisibilityCanBePrivate")
    override fun submit(formData: STATE) {
        intent { reduce { formData } }
    }

    override fun <V : Any> submitField(property: KProperty0<V>, value: V) {
        val factory = (currentState as? CopyBuilderFactory<*>)
            ?: error("The value class must be annotated with @KopyBuilder, and be compiled with KopyBuilder Compile Plugin!")
        val updated = factory.copyBuild {
            put(property.name, property.get())
        } as STATE
        this.submit(updated)
    }

    @OptIn(OrbitInternal::class)
    @OrbitDsl
    public suspend fun <SE : Any> SimpleSyntax<STATE, SE>.reduce(
        validate: Boolean = false,
        reducer: SimpleContext<STATE>.() -> STATE
    ) {
        containerContext.reduce { oldState ->
            val newState = SimpleContext(oldState).reducer()
            val success = this@ContainerFormHost.form.commit(newState, validate)
            if (success) newState else oldState
        }
    }

}
