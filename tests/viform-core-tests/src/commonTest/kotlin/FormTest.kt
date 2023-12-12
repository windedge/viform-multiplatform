package test

import io.github.windedge.viform.core.Form
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals


class FormTest {
    lateinit var person: User

    @BeforeTest
    fun setup() {
        person = User("hello", 1)
    }

    @Test
    fun testFormCreate() {
        val form: Form<User> = Form(person)
        val formField = form.registerField(User::name)
        form.submit(person.copy(name = "world"))

        assertEquals("world", formField.value)
    }

}