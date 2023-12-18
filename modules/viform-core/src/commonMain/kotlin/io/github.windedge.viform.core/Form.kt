package io.github.windedge.viform.core


import io.github.windedge.copybuilder.CopyBuilderHost
import kotlinx.coroutines.sync.Mutex
import kotlin.reflect.*


public interface Form<T : Any> {

    public fun <V> getOrRegisterField(property: KProperty<V>): FormField<V>

    public fun containsField(name: String): Boolean

    public fun <V> replaceField(formField: FormField<V>): FormField<V>

    public val fields: List<FormField<*>>

    public fun validate(): Boolean

    public fun submit(formData: T, validate: Boolean = true): Boolean

    public fun pop(): T

    public fun reset()
}

public fun <T : Any> Form(initialState: T): Form<T> {
    return FormImpl(initialState)
}

public fun <T : Any> Form(initialState: T, build: FormBuilder<T>.(T) -> Unit): Form<T> {
    val form = FormImpl(initialState)
    val builder = FormBuilder(form)
    builder.build(initialState)
    return form
}

internal class FormImpl<T : Any>(private val initialState: T) : Form<T> {
    private val mutex = Mutex()
    private val fieldsMap = mutableMapOf<String, FormField<*>>()

    override val fields: List<FormField<*>> get() = fieldsMap.values.toList()

    private fun <V> registerField(property: KProperty<V>): FormField<V> {
        if (fieldsMap.containsKey(property.name)) {
            error("The field cannot be registered repeatedly, field: ${property.name}")
        }
        val initialValue = when (property) {
            is KProperty0<*> -> (property as KProperty0<V>).get()
            is KProperty1<*, *> -> @Suppress("UNCHECKED_CAST") (property as KProperty1<T, V>).get(initialState)
            else -> error("Can't get the value from the property: ${property.name}")
        }
        val formField = FormFieldImpl(property.name, initialValue)
        fieldsMap[property.name] = formField
        return formField
    }

    override fun <V> getOrRegisterField(property: KProperty<V>): FormField<V> {
        if (fieldsMap.containsKey(property.name)) {
            @Suppress("UNCHECKED_CAST")
            return fieldsMap[property.name] as FormField<V>
        }
        return registerField(property)
    }

    override fun containsField(name: String): Boolean {
        return fieldsMap.containsKey(name)
    }

    override fun <V> replaceField(formField: FormField<V>): FormField<V> {
        if (!fieldsMap.containsKey(formField.name)) {
            error("The field is not registered, field: ${formField.name}")
        }

        @Suppress("UNCHECKED_CAST")
        return fieldsMap.put(formField.name, formField) as FormField<V>
    }

    override fun validate(): Boolean {
        return fieldsMap.values.map { it.validate() }.all { it }
    }

    override fun submit(formData: T, validate: Boolean): Boolean {
        @Suppress("UNCHECKED_CAST")
        formData as? CopyBuilderHost<T>
            ?: error("CopyBuilder is not implemented, please apply the `KopyBuilder` plugin")

        val builder = formData.toCopyBuilder()
        fieldsMap.filter { builder.contains(it.key) }.forEach {
            @Suppress("UNCHECKED_CAST")
            val field = it.value as FormField<Any?>
            val value = builder.get(it.key)
            field.setValue(value, false)
        }

        if (validate) return validate()
        return true
    }

    override fun pop(): T {
        val initialState = this.initialState.also {
            if (fieldsMap.isEmpty()) return it
        }

        @Suppress("UNCHECKED_CAST")
        initialState as? CopyBuilderHost<T>
            ?: error("CopyBuilder is not implemented, please apply the `KopyBuilder` plugin")

        val result = initialState.copyBuild {
            fieldsMap
                .filter { contains(it.key) }
                .forEach { (name, field) ->
                    put(name, field.currentValue)
                }
        }
        return result
    }

    override fun reset() {
        this.submit(this.initialState, validate = false)
    }

}


public class FormBuilder<T : Any>(public val form: Form<T>) {
    public fun <V> field(property: KProperty1<T, V>): FormField<V> {
        return form.getOrRegisterField(property)
    }

    public fun <V> field(
        property: KProperty1<T, V>,
        build: FormField<V>.() -> Unit
    ): FormField<V> {
        val field = form.getOrRegisterField(property)
        field.build()
        return field
    }

    /*

    public fun <V> field(property: KProperty0<V>): FormField<V> {
        return form.registerField(property)
    }

        public fun <V> field(
            property: KProperty0<V>,
            build: FormField<V>.() -> Unit
        ): FormField<V> {
            val field = form.registerField(property)
            field.build()
            return field
        }
    */
}
