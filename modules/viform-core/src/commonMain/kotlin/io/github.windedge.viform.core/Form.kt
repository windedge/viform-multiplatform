package io.github.windedge.viform.core


import io.github.windedge.copybuilder.CopyBuilderFactory
import kotlin.reflect.*

public interface Form<T : Any> {

    public fun <V : Any?> registerField(property: KProperty1<T, V>): FormField<V>

    public fun <V : Any?> registerField(property: KProperty0<V>): FormField<V>

    public fun <V : Any?> getFormField(property: KProperty1<T, V>): FormField<V>

    public fun <V : Any?> getFormField(property: KProperty0<V>): FormField<V>

    public fun <V : Any?> setFieldValue(property: KProperty1<T, V>, value: V, validate: Boolean = true)

    public fun <V : Any?> setFieldValue(property: KProperty0<V>, value: V, validate: Boolean = true)

    public fun validate(): Boolean

    public fun commit(formData: T, validate: Boolean = true): Boolean

    public fun pop(target: T? = null): T
}

public interface FormBuilder<T : Any> {
    public fun <V> field(property: KProperty1<T, V>): FormField<V>
}

public fun <T : Any> Form(initialState: T): Form<T> {
    return FormImpl(initialState)
}

internal class FormImpl<T : Any>(private val initialState: T) : Form<T> {
    private val fieldsMap = mutableMapOf<String, FormField<*>>()

    override fun <V : Any?> registerField(property: KProperty1<T, V>): FormField<V> {
        val initialValue = property.get(initialState)
        val formField = FormFieldImpl<T, V>(property.name, initialValue)
        fieldsMap[property.name] = formField
        return formField
    }

    override fun <V> registerField(property: KProperty0<V>): FormField<V> {
        val initialValue = property.get()
        val formField = FormFieldImpl<T, V>(property.name, initialValue)
        fieldsMap[property.name] = formField
        return formField
    }

    @Suppress("UNCHECKED_CAST")
    override fun <V : Any?> getFormField(property: KProperty1<T, V>): FormField<V> {
        if (fieldsMap.containsKey(property.name)) {
            return fieldsMap[property.name] as FormField<V>
        }
        return registerField(property)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <V> getFormField(property: KProperty0<V>): FormField<V> {
        if (fieldsMap.containsKey(property.name)) {
            return fieldsMap[property.name] as FormField<V>
        }
        return registerField(property)
    }

    override fun <V> setFieldValue(property: KProperty0<V>, value: V, validate: Boolean) {
        val formField = getFormField(property)
        formField.setValue(value, validate)
    }

    override fun <V> setFieldValue(property: KProperty1<T, V>, value: V, validate: Boolean) {
        val formField = getFormField(property)
        formField.setValue(value, validate)
    }

    override fun validate(): Boolean {
        val results = fieldsMap.map { it.key to it.value.validate() }.toMap()
        return results.all { it.value }
    }

    @Suppress("UNCHECKED_CAST")
    override fun commit(formData: T, validate: Boolean): Boolean {
        if (formData !is CopyBuilderFactory<*>) {
            error("The value class must be annotated with @KopyBuilder, and be compiled with KopyBuilder Compile Plugin!")
        }

        val builder = (formData as CopyBuilderFactory<*>).toCopyBuilder()
        fieldsMap.forEach {
            val value = builder.get(it.key)
            val field = it.value as FormField<Any?>
            field.setValue(value, validate)
        }
        return true
    }

    @Suppress("UNCHECKED_CAST")
    override fun pop(target: T?): T {
        val initialState = target ?: this.initialState
        if (initialState !is CopyBuilderFactory<*>) {
            error("CopyBuilder is not implemented, may be not using the compiler plugin?")
        }

        val result = (initialState as CopyBuilderFactory<T>).copyBuild {
            fieldsMap.forEach {
                put(it.key, it.value.value)
            }
        }
        return result
    }
}

internal class FormBuilderImpl<T : Any>(val form: Form<T>) : FormBuilder<T> {
    override fun <V> field(property: KProperty1<T, V>): FormField<V> {
        return form.registerField(property)
    }
}
