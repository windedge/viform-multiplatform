English | [中文](README-CN.md)

# ViForm

ViForm is a Kotlin library for form validation, mainly aimed at Compose Multiplatform projects. It has the following features:
* Supports Kotlin Multiplatform(android, jvm, js/wasmjs, ios)
* Type-safe validation rules, supporting both DSL style and chained style validation declaration
* Form state management
* Integrated support for Compose Multiplatform(android, desktop, wasmjs, ios)


## Prerequisites
- Kotlin Coroutine
- KopyBuilder
- Compose Multiplatform (optional)

## Installation

Apply the KopyBuilder Gradle plugin
```kotlin
plugins {
    id("io.github.windedge.kopybuilder") version "0.2.4"
}
```

Add dependency to Multiplatform project
```Kotlin
kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation("io.github.windedge.viform:viform-core:0.2.2")

                // Optional
                implementation("io.github.windedge.viform:viform-compose:0.2.2")
            }
        }
    }
}
```

Add dependency to JVM project
```Kotlin
dependencies {
    implementation("io.github.windedge.viform:viform-core:0.2.2")

    // Optional
    implementation("io.github.windedge.viform:viform-compose:0.2.2")
}
```

## Quick Start

Define validation rules:

```kotlin
import io.github.windedge.copybuilder.KopyBuilder
import io.github.windedge.viform.core.*


@KopyBuilder
data class Signup(
    val name: String = "",
    val email: String? = null,
    val password: String = "",
    val confirmPassword: String = "",
)


val schema = FormSchema.create<Signup> {
   field(Signup::name) {
       required("User name is required.")
       isAlphaNumeric()
   }

   field(Signup::email).optional {
       isEmail()
   }

   // Chain style
   field(Signup::password).required().lengthBetween(8, 20)

   // Custom rule
   field(Signup::confirmPassword).required().lengthBetween(8, 20)
       .custom("Passwords must be the same.") {
           it == field(Signup::password).currentValue
       }
}
```

Use in Jetpack Compose project:

```kotlin
import io.github.windedge.viform.compose.use

@Compose
fun SignupApp() {
    val form = schema.buildForm(Signup())
    Column {
        form.use {
            field(it::name) {
                TextInput("User Name: ", currentValue, hasError, errorMessage, onValueChange = {
                    setValue(it, validate = true)  // validate synchronously
                })
            }

            field(it::email) {
                watchLazily { validate() }  // validate asynchronously
                TextInput("Email:", currentValue ?: "", hasError, errorMessage, onValueChange=::setValue)
            }

            field(it::password) {
                watchLazily { if (it.isNotEmpty()) validate() }
                TextInput("Password:", currentValue, hasError, errorMessage, onValueChange=::setValue)
            }

            field(it::confirmPassword) {
                watchLazily { if (it.isNotEmpty()) validate() }
                TextInput("Confirm Password:", currentValue, hasError, errorMessage, onValueChange=::setValue)
            }

            // validate on submit
            Button(onClick = {
                if(form.validate()) {
                    val signup = form.pop()   // get form data
                    // ...
                }
            }) { Text("Sign up") }
        }
    }
}
```


## Known Issues

When compiling the WebAssembly (WASM) target, you may encounter the error `Can't link symbol kotlin.internal.ir/ANDAND`. 

## License
[MIT License](./LICENSE)

## Author

[Ken Xu](https://github.com/windedge)
