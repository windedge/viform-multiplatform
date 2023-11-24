package local.sandbox

import io.github.windedge.copybuilder.KopyBuilder
import io.github.windedge.viform.core.Form
import io.github.windedge.viform.core.platform
import java.time.LocalDate

@KopyBuilder
data class Project(val name: String, val startAt: LocalDate)

fun main() {
    println("hello, platform: $platform")

    val project = Project("test", LocalDate.now())
    val form = Form(project)
    form.registerField(Project::name)
    form.registerField(Project::startAt)

    form.setFieldValue(Project::name, "hello")
    form.setFieldValue(Project::startAt, LocalDate.now().plusDays(1L))

    val newProject = form.pop()
    println("newProject = ${newProject}")
}
