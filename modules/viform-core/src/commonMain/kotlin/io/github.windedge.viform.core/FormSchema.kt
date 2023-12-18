package io.github.windedge.viform.core

public class FormSchema<T : Any> private constructor(private val builderBlock: FormBuilder<T>.() -> Unit) {

    public companion object {
        public fun <T : Any> create(build: FormBuilder<T>.() -> Unit): FormSchema<T> {
            val formSchema = FormSchema<T>(build)
            return formSchema
        }
    }

    public fun buildForm(formData: T): Form<T> {
        val form = FormImpl(formData).apply {
            FormBuilder<T>(this).apply(builderBlock)
        }
        return form
    }
}
