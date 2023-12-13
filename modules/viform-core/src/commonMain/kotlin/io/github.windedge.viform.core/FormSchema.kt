package io.github.windedge.viform.core

import kotlin.reflect.KProperty1

public class FormSchema<T : Any> private constructor() {

    public companion object {
        public fun <T : Any> create(build: FormSchemaBuilder<T>.() -> Unit): FormSchema<T> {
            val formSchema = FormSchema<T>()
            val builder = FormSchemaBuilder<T>(formSchema)
            builder.build()
            return formSchema
        }
    }

    private val fieldDescriptors = mutableMapOf<String, FieldDescriptor<T, *>>()

    private fun <V : Any?> registerField(property: KProperty1<T, V>): FieldDescriptor<T, V> {
        val fieldDescriptor = FieldDescriptor(property)
        fieldDescriptors[property.name] = fieldDescriptor
        return fieldDescriptor
    }

    public fun <V : Any?> getOrRegisterField(property: KProperty1<T, V>): FieldDescriptor<T, V> {
        if (fieldDescriptors.containsKey(property.name)) {
            @Suppress("UNCHECKED_CAST")
            return fieldDescriptors[property.name] as FieldDescriptor<T, V>
        }
        return registerField(property)
    }

    public fun containsField(name: String): Boolean = fieldDescriptors.containsKey(name)

    public fun getFields(): List<FieldDescriptor<T, *>> = fieldDescriptors.values.toList()

    public fun buildForm(formData: T): Form<T> {
        val form = FormImpl(formData).apply {
            fieldDescriptors.forEach { (_, fieldDescriptor) ->
                val formField = this.getOrRegisterField(fieldDescriptor.property)
                fieldDescriptor.getValidators().forEach {
                    @Suppress("UNCHECKED_CAST")
                    formField.addValidator(it as FieldValidator<Any?>)
                }
            }
        }
        return form
    }
}

public class FieldDescriptor<T : Any, V>(public val property: KProperty1<T, V>) : ValidatorContainer<V> {
    public val name: String get() = property.name

    private val validators = mutableListOf<FieldValidator<V>>()

    public override fun getValidators(): List<FieldValidator<V>> = validators.toList()

    public override fun addValidator(validator: FieldValidator<V>) {
        validators.add(validator)
    }

    public override fun clearValidators() {
        validators.clear()
    }
}


public class FormSchemaBuilder<T : Any>(private val formSchema: FormSchema<T>) {

    public fun <V> field(property: KProperty1<T, V>): FieldDescriptor<T, V> {
        return formSchema.getOrRegisterField(property)
    }

    public fun <V> field(
        property: KProperty1<T, V>,
        build: FieldDescriptor<T, V>.() -> Unit
    ): FieldDescriptor<T, V> {
        val field = formSchema.getOrRegisterField(property)
        field.build()
        return field
    }
}
