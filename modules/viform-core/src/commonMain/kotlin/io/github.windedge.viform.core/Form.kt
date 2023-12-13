package io.github.windedge.viform.core


import io.github.windedge.copybuilder.CopyBuilderHost
import kotlin.reflect.*


public interface Form<T : Any> {

    public fun <V : Any?> registerField(property: KProperty1<T, V>): FormField<V>

    public fun <V : Any?> registerField(property: KProperty0<V>): FormField<V>

    public fun <V : Any?> getField(property: KProperty1<T, V>): FormField<V>

    public fun <V : Any?> getField(property: KProperty0<V>): FormField<V>

    public fun <V : Any?> setFieldValue(property: KProperty1<T, V>, value: V, validate: Boolean = false)

    public fun <V : Any?> setFieldValue(property: KProperty0<V>, value: V, validate: Boolean = false)

    public fun containsField(name: String): Boolean

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
    builder.build(form.pop())
    return form
}

internal class FormImpl<T : Any>(private val initialState: T) : Form<T> {
    private val fieldsMap = mutableMapOf<String, FormField<*>>()

    override val fields: List<FormField<*>> get() = fieldsMap.values.toList()

    override fun <V : Any?> registerField(property: KProperty1<T, V>): FormField<V> {
        val initialValue = property.get(initialState)
        val formField = FormFieldImpl(property.name, initialValue)
        fieldsMap[property.name] = formField
        return formField
    }

    override fun <V> registerField(property: KProperty0<V>): FormField<V> {
        if (fieldsMap.containsKey(property.name)) {
            error("The field cannot be registered duplicately, field: ${property.name}")
        }
        val initialValue = property.get()
        val formField = FormFieldImpl(property.name, initialValue)
        fieldsMap[property.name] = formField
        return formField
    }

    @Suppress("UNCHECKED_CAST")
    override fun <V : Any?> getField(property: KProperty1<T, V>): FormField<V> {
        if (fieldsMap.containsKey(property.name)) {
            return fieldsMap[property.name] as FormField<V>
        }
        return registerField(property)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <V> getField(property: KProperty0<V>): FormField<V> {
        if (fieldsMap.containsKey(property.name)) {
            return fieldsMap[property.name] as FormField<V>
        }
        return registerField(property)
    }

    override fun <V> setFieldValue(property: KProperty0<V>, value: V, validate: Boolean) {
        val formField = getField(property)
        formField.update(value, validate)
    }

    override fun <V> setFieldValue(property: KProperty1<T, V>, value: V, validate: Boolean) {
        val formField = getField(property)
        formField.update(value, validate)
    }

    override fun containsField(name: String): Boolean {
        return fieldsMap.containsKey(name)
    }

    override fun validate(): Boolean {
        val results = fieldsMap.map { it.key to it.value.validate() }.toMap()
        return results.all { it.value }
    }

    @Suppress("UNCHECKED_CAST")
    override fun submit(formData: T, validate: Boolean): Boolean {
        if (formData !is CopyBuilderHost<*>) {
            error("CopyBuilder is not implemented, please apply the `KopyBuilder` plugin")
        }

        val builder = (formData as CopyBuilderHost<T>).toCopyBuilder()
        fieldsMap.filter { builder.contains(it.key) }.forEach {
            val field = it.value as FormField<Any?>
            val value = builder.get(it.key)
            field.update(value, false)
        }

        if (validate) return validate()
        return true
    }

    @Suppress("UNCHECKED_CAST")
    override fun pop(): T {
        val initialState = this.initialState
        if (initialState !is CopyBuilderHost<*>) {
            error("CopyBuilder is not implemented, please apply the `KopyBuilder` plugin")
        }

        if (fieldsMap.isEmpty()) {
            return initialState
        }
        val result = (initialState as CopyBuilderHost<T>).copyBuild {
            fieldsMap.filter {
                contains(it.key)
            }.forEach { (name, field) ->
                put(name, field.value)
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
        return form.registerField(property)
    }

    public fun <V> field(
        property: KProperty1<T, V>,
        build: FormField<V>.() -> Unit
    ): FormField<V> {
        val field = form.registerField(property)
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
