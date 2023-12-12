package io.github.windedge.viform.compose

import kotlin.reflect.*

internal class DummyProperty<V>(private val propertyName: String, private val propertyValue: V) : KProperty0<V> {
    override val annotations: List<Annotation>
        get() = error("Not yet implemented")
    override val getter: KProperty0.Getter<V>
        get() = error("Not yet implemented")
    override val isAbstract: Boolean
        get() = error("Not yet implemented")
    override val isConst: Boolean
        get() = error("Not yet implemented")
    override val isFinal: Boolean
        get() = error("Not yet implemented")
    override val isLateinit: Boolean
        get() = error("Not yet implemented")
    override val isOpen: Boolean
        get() = error("Not yet implemented")
    override val isSuspend: Boolean
        get() = error("Not yet implemented")
    override val name: String
        get() = propertyName
    override val parameters: List<KParameter>
        get() = error("Not yet implemented")
    override val returnType: KType
        get() = error("Not yet implemented")
    override val typeParameters: List<KTypeParameter>
        get() = error("Not yet implemented")
    override val visibility: KVisibility
        get() = error("Not yet implemented")

    override fun call(vararg args: Any?): V {
        error("Not yet implemented")
    }

    override fun callBy(args: Map<KParameter, Any?>): V {
        error("Not yet implemented")
    }

    override fun get(): V {
        return propertyValue
    }

    override fun getDelegate(): Any? {
        error("Not yet implemented")
    }

    override fun invoke(): V {
        return propertyValue
    }

}