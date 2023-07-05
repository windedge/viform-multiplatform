package io.github.windedge.viform.core


import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty0
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties


interface Form<T : Cloneable<T>> {

    fun <V : Any?> registerField(property: KProperty1<T, V>): FormField<V>

    fun <V : Any?> registerField(property: KProperty0<V>): FormField<V>

    fun <V : Any?> getFormField(property: KProperty1<T, V>): FormField<V>

    fun <V : Any?> getFormField(property: KProperty0<V>): FormField<V>

    fun <V : Any?> setFieldValue(property: KProperty1<T, V>, value: V, validate: Boolean = true)

    fun <V : Any?> setFieldValue(property: KProperty0<V>, value: V, validate: Boolean = true)

    fun validate(): Boolean

    fun commit(formData: T, validate: Boolean = true): Boolean

    fun populate(target: T? = null): T
}

interface FormBuilder<T : Any> {
    fun <V> field(property: KMutableProperty1<T, V>): FormField<V>
}


internal class FormImpl<T : Cloneable<T>>(private val initialState: T) : Form<T> {
    private val fieldsMap = mutableMapOf<String, FormField<*>>()

    override fun <V : Any?> registerField(property: KProperty1<T, V>): FormField<V> {
        val initialValue = property.get(initialState)
        val formField = FormFieldImpl<T, V>(property.name, initialValue)
        fieldsMap[property.name] = formField
        return formField
    }

    override fun <V : Any?> registerField(property: KProperty0<V>): FormField<V> {
        val initialValue = property.get()
        val formField = FormFieldImpl<T, V>(property.name, initialValue)
        fieldsMap[property.name] = formField
        return formField
    }

    override fun <V : Any?> getFormField(property: KProperty1<T, V>): FormField<V> {
        if (fieldsMap.containsKey(property.name)) {
            return fieldsMap[property.name] as FormField<V>
        }
        return registerField(property)
    }

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

    override fun commit(formData: T, validate: Boolean): Boolean {
        fieldsMap.forEach {
            val fieldName = it.key
            val property = formData::class.memberProperties
                .first { prop -> prop.name == fieldName } as KProperty1<T, Any?>
            val value = property.get(formData)
            (it.value as FormField<Any?>).setValue(value, false)
        }

        return if (validate) validate() else true
    }

    override fun populate(target: T?): T {
        val result = target?.clone() ?: this.initialState.clone()
        fieldsMap.forEach {
            val fieldName = it.key
            val property = result::class.memberProperties
                .filterIsInstance<KMutableProperty1<*, *>>()
                .first { prop -> prop.name == fieldName } as KMutableProperty1<T, Any?>
            property.set(result, it.value.value)
        }
        return result
    }
}

internal class FormBuilderImpl<T : Cloneable<T>>(val form: Form<T>) : FormBuilder<T> {
    override fun <V> field(property: KMutableProperty1<T, V>): FormField<V> {
        return form.registerField(property)
    }
}
