package local.sandbox

//import io.github.windedge.viform.core.CoreLib
//import io.github.windedge.viform.dsl.withPlatform
//import io.github.windedge.viform.dsl.withPlatformSuspend
import kotlinx.coroutines.runBlocking
import io.github.windedge.viform.core

fun main() {
/*
  val core = CoreLib()
  println(core.sampleApi())
  println(core.withPlatform("Blocking"))
  runBlocking {
    suspendingMain()
  }
*/
    println("hello, platform: $platform")
}

suspend fun suspendingMain() {
/*
  val core = CoreLib()
  println(core.sampleSuspendApi())
  println(core.withPlatformSuspend("Suspending"))
*/
}
