# ViForm

ViForm是一个用于表单验证的Kotlin库，主要目标是用在Compose Multiplatform项目，它具有下面几个特点：
* 支持Kotlin多平台
* 类型安全的验证规则，同时支持DSL风格和链式风格的验证规则写法
* 表单状态管理
* Compose Multiplatform的集成支持


## 前提条件
- Kotlin Coroutine
- KopyBuilder
- Compose Multiplatform(可选)

## 安装

应用KopyBuilder的Gradle插件
```kotlin
plugins {
    id("io.github.windedge.kopybuilder") version "0.1.5"
}
```

在Multiplatform项目添加信赖
```Kotlin
kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation("io.github.windedge.viform:viform-core:$VERSION")

                // 可选
                implementation("io.github.windedge.viform:viform-compose:$VERSION")
            }
        }
    }
}
```

在JVM项目添加信赖
```Kotlin
dependencies {
    implementation("io.github.windedge.viform:viform-core:$VERSION")

    // 可选
    implementation("io.github.windedge.viform:viform-compose:$VERSION")
}
```

## 快速开始

定义验证规则：

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

   // 链式风格
   field(Signup::password).required().lengthBetween(8, 20)

   // 自定义规则
   field(Signup::confirmPassword).required().lengthBetween(8, 20)
       .custom("Passwords must be the same.") {
           it == field(Signup::password).currentValue
       }
}
```

在Jetpack Compose项目中使用：

```kotlin
import io.github.windedge.viform.compose.use

@Compose
fun SignupApp() {
    val form = schema.buildForm(Signup())
    Column {
        form.use() {
            field(it::name) {
                TextInput("User Name: ", currentValue, hasError, errorMessage, onValueChange = {
                    setValue(it, validate = true)  // 实时验证
                })
            }

            field(it::email) {
                watchLazily { validate() }  // 延迟验证
                TextInput("Email:", currentValue ?: "", hasError, errorMessage), onValueChange=::setValue)
            }

            field(it::password) {
                watchLazily { if (it.isNotEmpty()) validate() }
                TextInput("Password:", currentValue, hasError, errorMessage, onValueChange=::setValue)
            }

            field(it::confirmPassword) {
                watchLazily { if (it.isNotEmpty()) validate() }
                TextInput("Confirm Password:", currentValue, hasError, errorMessage, onValueChange=::setValue)
            }

            // 提交时验证
            Button(onClick = {
                if(form.validate()) {
                    val data = form.pop()   // 获取数据
                    // ...
                }
            }) { Text("Sign up") }
        }
    }
}
```


[//]: # (## 构建状态)

[//]: # ()
[//]: # (这里可以放置CI/CD构建状态的徽章。)

[//]: # ()
[//]: # (## 贡献)

[//]: # ()
[//]: # (如果你想贡献代码，请阅读`CONTRIBUTING.md`。)

## 许可证
[MIT License](./LICENSE)

## 作者

[Ken Xu](https://github.com/windedge)

[//]: # (## 致谢)

[//]: # ()
[//]: # (感谢所有贡献者的努力。)