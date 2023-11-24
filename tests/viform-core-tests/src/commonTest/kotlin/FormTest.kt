package io.github.windedge.viform.core


import io.github.windedge.copybuilder.KopyBuilder
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals


@KopyBuilder
data class Person(
    val name: String,
    val age: Int?,
)


class FormTest {
    lateinit var person: Person

    @BeforeTest
    fun setup() {
        person = Person("hello", 1)
    }

    @Test
    fun testFormCreate() {
        val form: Form<Person> = Form(person)
        val formField = form.registerField(Person::name)
        form.commit(person.copy(name = "world"))

        assertEquals("world", formField.value)
    }

}