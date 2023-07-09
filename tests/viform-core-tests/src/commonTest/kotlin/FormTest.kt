package io.github.windedge.viform.core


import io.kotest.assertions.json.schema.JsonSchema
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import kotlinx.serialization.json.Json.Default.decodeFromJsonElement
import kotlin.test.BeforeTest
import kotlin.test.Test


@Serializable
data class Person(
    val name: String,
    val age: Int,
) : Cloneable<Person> {
    override fun clone(): Person {
        return this.copy()
    }
}

fun <T> copy(data : T, name: String): T {
    val json = Json.encodeToJsonElement(data as Any).jsonObject.toMutableMap()
    json.toMutableMap()["name"] = JsonPrimitive(name)
    val jsonObject = buildJsonObject {
        json.forEach {
            put(it.key, it.value)
        }
    }
//    return Json.decodeFromJsonElement(jsonObject)
    error("...")
}

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
    }

    @Test
    fun testJson() {

    }
}