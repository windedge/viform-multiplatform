package test

import io.github.windedge.viform.core.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FormSchemaTest {

    @Test
    fun testCreate() {
        val schema = FormSchema.create {
            field(User::name) {
                required()
                isAlphaNumeric()
            }
            field(User::age).nullable {
                greaterThan(0)
            }
        }

        val form: Form<User> = schema.buildForm(User(name = "123", age = null))
        assertTrue { form.pop().name == "123" }
        assertFalse { form.validate() }

        form.submit(User("Amy", age = -1))
        assertFalse { form.validate() }

        form.submit(User("Amy123", age = null))
        assertTrue { form.validate() }

        form.submit(User("Amy123", age = 1))
        assertTrue { form.validate() }
    }
}