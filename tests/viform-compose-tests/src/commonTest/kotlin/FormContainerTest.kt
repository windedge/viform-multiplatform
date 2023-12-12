import io.github.windedge.copybuilder.KopyBuilder
import io.github.windedge.viform.compose.useForm
import io.github.windedge.viform.core.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


class FormContainerTest {

    @KopyBuilder
    data class User(val name: String, val email: String?)

    @Test
    fun testFormCreate() {
        val user = User("Ken", "x@y.com")

        val host = SimpleFormHost(user)
        host.form.registerField(User::name).apply {
            required()
            custom { true }
        }

        host.useForm {
            field(it::name) {
                setValue("hello", true)
            }
        }
        host.form.validate()
        val newUser = host.form.pop()
        assertEquals("hello", newUser.name)
    }
}