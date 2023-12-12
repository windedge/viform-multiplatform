import io.github.windedge.copybuilder.KopyBuilder
import io.github.windedge.viform.compose.use
import io.github.windedge.viform.core.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


class FormHostTest {

    @KopyBuilder
    data class User(val name: String, val email: String?)

    @Test
    fun testFormCreate() {
        val user = User("Ken", "x@y.com")
        val form = Form(user) {
            field(it::name) {
                required()
                isAlphaNumeric()
            }
            field(it::email).nullable {
                isEmail()
            }
        }

        val host = SimpleFormHost(form)
        form.use {
            field(it::name) {
                setValue("hello", true)
            }
        }
        val newUser = host.form.pop()
        assertEquals("hello", newUser.name)
    }
}