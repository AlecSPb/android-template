# android-template
Template repo with various common components, to reduce "project setup" time

1. [Single navigation graph](https://github.com/Skyyo/android-template)
2. [Single navigation graph & Room ](https://github.com/Skyyo/android-template/tree/room)
3. [Single navigation graph & Proto Store ](https://github.com/Skyyo/android-template/tree/proto_store)
4. [Bottom navigation view](https://github.com/Skyyo/android-template/tree/bottom_navigation_view)
5. [Bottom navigation view & Room ](https://github.com/Skyyo/android-template/tree/bottom_navigation_view_room)
6. [Bottom navigation view & Proto Store ](https://github.com/Skyyo/android-template/tree/bottom_navigation_view_proto)

After setup:
1. Change package name everywhere ( including proto files )
2. Update readme.md


# During developement (Code style/approaches recomendations)

* Always attempt to have a single line ```if/else``` statement. In that case braces aren't needed. If the ```if/else``` expression exceeds single line - please use braces. If only an ```if``` is used and expression is a single line - braces aren't needed. 
```kotlin
private fun provideCardColor(): Int = if (x > y) Color.CYAN else Color.BLUE
```
```kotlin
if (condition == true) doSmth()
..
```
instead of:
```kotlin
private fun validateSmth() {
        if (condition == true) state.postValue(stateX)
        else events.offer(PasswordNotValid)
    }
``` 
use:
```kotlin
private fun validateSmth() {
        if (condition == true) {
            state.postValue(stateX)
        } else {
            events.offer(PasswordNotValid)
        }
    }
```    

* Rule of a thumb: if we access the same object 3 or more times - please use ```apply```,```with```. If we want to hide big, non-priority (glance wise) code chunks - there is nothing wrong to use ```apply```, with even a 1 line under it.
* Explicitly specify function return types if function output type isn't obvious from function name, or functions code block at first glance.

instead of:
```kotlin
private fun provideSomeValue() = (12f * 23).toInt() + 123
```
use:
```kotlin
private fun provideSomeValue(): Int = (12f * 23).toInt() + 123
```

* It's ok to use [trailing commas](https://kotlinlang.org/docs/reference/coding-conventions.html#trailing-commas). They are there to make our life easier.
```kotlin
data class SignUpRequest(
    val email: String,
    val firstName: String,
)
```

* Use [typealiases](https://kotlinlang.org/docs/reference/type-aliases.html#type-aliases) if type name is too long or we have a lot of recurring lambda types.
```typealias``` should be declared in an appropriate scope. If used in a single place - it can be placed on top of the same class. If it's a common lambda, which can be reused across different feature packages - please create ```CommonTypeAliases``` file under common package.

instead of:
```kotlin
class ShopProductItem(
    private val onClick: (position: Int) -> Unit
)
```
use 
```kotlin
typealias OnClick = (position: Int) -> Unit
class ShopProductItem(
    private val onClick: OnClick
)
```

* We are extensively using ```tryOrNull```extension when we don't need to check the exception reasons from ```try/catch``` blocks, which allows us to write concise statements like in example below, by using kotlins nullability with [elvis operator](https://kotlinlang.org/docs/reference/null-safety.html#elvis-operator)
```kotlin
suspend fun checkIfEmailExists(email: String): EmailExistsResult {
        val result = tryOrNull { authCalls.checkIfEmailExists(email) }
        result ?: return EmailExistsNetworkError
        return if (result.isUserRegisteredByEmail) EmailExists else EmailNotFound
    }
```

* Handle [process death](https://developer.android.com/topic/libraries/architecture/saving-states#onsaveinstancestate). We are using [SavedStateHandle](https://developer.android.com/topic/libraries/architecture/viewmodel-savedstate) inside viewModel 99% of the time. There is difference between return types. We need to manually save the values when using ```non-liveData``` fields. ```LiveData``` value reassignments will be automatically reflected inside ```savedStateHandle```. For testing purposes please use [venom](https://github.com/YarikSOffice/venom).
```kotlin
val state = handle.getLiveData("viewState", 0)
state.postValue(1) // doing so will automatically give us value of 1 upon PD

var scanCompleted = handle.get<Boolean>("scanCompleted") ?: false
        set(value) {
            field = value
            handle.set("scanCompleted", value) // set the key/value pair to the bundle upon each value reassignment
        }
scanCompleted = true // will be "false" after PD, unless we set the key/value pair to the bundle like above

```

* Declare custom classes instead of [Pairs](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/#pair), [Tripples](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-triple/).

instead of:
```kotlin
val timeRange: Pair<Int, Int>? = null
```
use:
```kotlin
class TimeRange(val from: Int, val to: Int)
val timeRange: TimeRange? = null
```

* When composing objects, consider the following approach:

instead of:
```kotlin
when (val response = authRepo.verifyCode(
            VerifyCodeRequest(
                email, pin,
                "someValue", "someValue",
                "someValue", "someValue"
            )
        )) {
            is VerifyCodeSuccess -> signIn()
            ..
        }
```
use:
```kotlin
val requestBody = VerifyCodeRequest(email, pin)
        when (val response = authRepo.verifyCode(requestBody)) {
            is VerifyCodeSuccess -> signIn()
            ..
        }
        //if object constructor has many arguments,or has some additional logic - move it into provideX() function, like this:
val requestBody = provideVerifyCodeRequestBody()
        when (val response = authRepo.verifyCode(requestBody)) {
            is VerifyCodeSuccess -> signIn()
            ..
        }
```

* Be pragmatic with [Kotlin Named Arguments](https://kotlinlang.org/docs/functions.html#named-arguments). Use them to make parts that are not self documenting easier to read:
```kotlin
class SignUpRequestBody(val email: String, val password: String)
val requestBody = SignUpRequestBody(email, password)

class ItemDecorator(val paddingTop: Int, val paddingLeft: Int, val paddingBottom: Int)
val itemDecorator = ItemDecorator(
    paddingTop = 16,
    paddingLeft = 8,
    paddingBottom = 2
)
```

* Working with dates/times is done via [java.time](https://docs.oracle.com/javase/8/docs/api/java/time/package-summary.html#package.description). No need for [ThreeTenABP](https://github.com/JakeWharton/ThreeTenABP) or ```java.util.date``` anymore.
For API < 26 versions - just [enable desugaring](https://developer.android.com/studio/write/java8-support#library-desugaring). Also don't be fast with creating extensions, first make yourself familiar with already available methods. There are plenty examples out there, like [this one](https://www.baeldung.com/java-8-date-time-intro).
* Always attempt to document complex code blocks, custom views, values that represent "types" in network responses, logical flows, etc.
* Remember to set the ```recyclerView``` adapter to ```null``` in ```onDestroyView()```, in cases where free memory is preffered over single adapter initialization.

# Continuous integration & pull requests
* Template already has a few GitHub Actions workflows included. Please ensure you're passing the checks locally, before opening pull request. To do that, either run commands in the IDE terminal, or setup a github hook. Commands are: ```./gradlew ktlintFormat```, ```./gradlew detektDebug```. <b>Request a review only after the CI checks have passed successfully</b>.
* If pull request contains code that should close the issue, please write: ```close #1, #2, #3```(number == issue number) somewhere in the PR description. This allows for automatic issue closing upon successfull PR merge.

# Before release
* Check the app for [overdrawing](https://developer.android.com/topic/performance/rendering/overdraw) regions, and optimize wherever possible.
* Check all ```recyclerViews``` usages and apply ```setHasFixedSize(true)``` wherever applicable.
* Run IDE's ```remove unused resources```. Be carefull to check the changes before commiting, so you don't accidentaly remove classes, which are just temporarily unused.
* Run IDE's ```convert png's to webp's```.
* Check the [r8](https://developer.android.com/studio/build/shrink-code#enable) rules to prevent release .apk/.aab issues as much as possible.
* It won't hurt to use [canary leak](https://square.github.io/leakcanary/) to check whether you don't have serious issues with memory leaks

# Additonal advices
* Invest some time into getting used to [IDE shortcuts](https://developer.android.com/studio/intro/keyboard-shortcuts). Doing so will save you a lot of time.
