# 3 함수정의와 호출

### 4.1 클레스 계층 정의
- 코틀린은 클래스 이름뒤에 콜론(:)을 이름을 적는것으로 상속과 인터페이스를 모두 처리한다.
- 코틀린에서는 override 키워드를 사용하여 오버라이드를 명시적으로 표시한다.

```kotlin
interface Clickable {
    fun click()
    fun showOff() = println("I'm clickable!")
}

interface Focusable {
    fun showOff() = println("I'm focusable!")
}

class Button : Clickable, Focusable {
  override fun click() =  println("I was clicked!")
  override fun showOff() {
        super<Clickable>.showOff()
        super<Focusable>.showOff()
  }
}

fun main(args: Array<String>) {
  val button = Button()
  button.showOff()
  button.click()
}
```


#### 4.1.2 open, final, abstract 변경자: 기본적으로 final
- 코틀린 클래스는 기본적으로 final 이다. 상속을 허용하려면 open 변경자를 붙여야한다.

```kotlin
open class RichButton : Clickable {
    fun disable() {} // final 메서드
    open fun animate() {} // open 메서드
    override fun click() {} // 상위 클래스의 메서드를 오버라이드 하려면 기본적으로 오버라이드를 허용한다.
}
```

- 추상클래스 (abstract class) 및 추상메서드는 항상 open되어있다.

| 키워드     | 역할                                                 | 기본값      | 사용 시 제한                             |
|------------|------------------------------------------------------|-------------|------------------------------------------|
| `final`    | 상속/재정의 금지                                      | 기본값      | 명시적으로 상속/재정의 허용 필요         |
| `open`     | 상속/재정의 허용                                      | 명시 필요   | 상속/재정의를 허용하려는 멤버에만 사용   |
| `abstract` | 구현 없는 추상 멤버 또는 클래스 정의                   | 명시 필요   | 반드시 하위 클래스에서 구현해야 함       |
| `override` | 상위 클래스/인터페이스의 멤버를 재정의할 때 사용       | 명시 필요   | 재정의 대상이 상위 클래스나 인터페이스여야 함 |

#### 4.1.3 가시성 변경자: 기본적으로 공개

# 코틀린의 기본 가시성 변경자

Kotlin에서는 클래스, 멤버(프로퍼티, 메서드 등)에 가시성을 설정하기 위해 가시성 변경자(Visibility Modifiers)를 제공합니다. 아래는 기본 가시성 변경자와 그 특징을 정리한 표입니다.

| 가시성 변경자      | 기본값  | 접근 범위                                                                                  | 설명                                                                                     |
|--------------------|---------|-------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------|
| `public`          | **기본값** | 모든 곳에서 접근 가능                                                                       | 별도로 명시하지 않을 경우 기본 가시성입니다.                                             |
| `internal`        | 명시 필요 | **같은 모듈** 내에서만 접근 가능                                                            | 모듈은 컴파일 단위(예: 한 JVM 모듈, 하나의 Gradle 모듈)입니다.                           |
| `protected`       | 명시 필요 | **하위 클래스**에서만 접근 가능                                                             | 클래스 멤버에만 사용 가능합니다. (클래스 자체에는 사용 불가)                             |
| `private`         | 명시 필요 | **같은 클래스** 또는 **같은 파일** 내에서만 접근 가능                                       | 멤버의 경우 선언된 클래스 내에서만 접근 가능하며, 파일 수준 멤버는 같은 파일에서만 접근 가능 |

- `internal` : 같은 모듈 내에서만 접근 가능, 자바에서는 internal이라는 가시성 변경자가 없기 public 으로 컴파일된다.

```kotlin
internal class TalkativeButton : Focusable {
    private fun yell() = println("Hey!")
    protected fun whisper() = println("Let's talk!")
}
```

#### 4.1.4 내부 클래스와 중첩된 클래스: 기본적으로 중첩 클래스

- 코틀린에서는 중첩 클래스를 기본으로 사용한다. 중첩 클래스는 외부 클래스의 멤버에 접근할 수 없다.
- | 특성                    | Java 기본 클래스       | Kotlin 기본 클래스    | Java static 클래스   | Kotlin `inner` 클래스 |
  |-------------------------|------------------------|-----------------------|----------------------|-----------------------|
  | 외부 클래스 인스턴스 필요 | **필요**               | **필요 없음**          | **필요 없음**         | **필요**              |
  | 선언 키워드              | 없음                   | `class`               | `static`             | `inner`              |
  | 외부 클래스 접근 가능    | **가능**               | **불가능**             | **불가능**            | **가능**             |

inner안에서 바깥 클래스에 접근할때는 this@를 사용한다.
```kotlin
class Outer {
    inner class Inner {
        fun getOuterReference(): Outer = this@Outer
    }
}
```

#### 4.1.5 sealed 클래스: 상속 제한
- 기본적으로 `sealed` 클래스는 `abstract`,  직접 인스턴스를 생성할 수 없고, 하위 클래스만 인스턴스화 가능
- 컴파일러는 `when` 식에서 `sealed` 클래스의 모든 하위 클래스를 검사하여, **완전한 분기**가 이루어졌는지 확인

<br />

### 4.2 뻔하지 않은 생성자와 프로퍼티를 갖는 클래스 선언

#### 4.2.1 클래스 초기화: 주 생성자와 초기화 블록
- 주 생성자는 클래스 헤더에 선언한다. (클래스 이름 뒤에 콜론(:)을 사용하여 선언)
- 초기화 블록은 초기화 로직을 담고있는 코드 블록이다. (init 키워드로 선언)

```kotlin
class User(_nickname: String) { // 주 생성자
    val nickname: String
    
    init {
      nickname = _nickname
    }
}

class User(val nickname: String) // 주 생성자와 프로퍼티 선언을 한번에

class User(val nickname: String, val isSubscribed: Boolean = true) // 디폴트 값이 있는 프로퍼티

open class User(val nickname: String) // 상속을 허용하는 클래스
class TwitterUser(nickname: String) : User(nickname) // 상속받는 클래스
```
- 모든 생성자 파라미터에 디폴트 값을 지정하면 컴파일러가 자동으로 파리미터가 없는 생성자를 생성한다.
- 클래스를 정의할때 별도로 생성자를 정의하지않는경우 디폴트 생성자가 생긴다.

| 상황                        | `()` 사용 여부 | 이유                                                  |
|-----------------------------|----------------|------------------------------------------------------|
| 기본 생성자가 있는 경우      | 사용           | 기본 생성자를 호출해야 하기 때문                     |
| 기본 생성자가 없는 경우      | 사용 안 함      | 보조 생성자를 통해 상위 클래스 생성자를 명시적으로 호출 |
| 추상 클래스 상속             | 사용 안 함      | 추상 클래스는 생성자를 호출할 필요가 없기 때문        |


#### 4.2.2 부 생성자: 클래스에 여러 생성자 추가
- 부 생성자는 constructor 키워드로 선언한다.
- 부 생성자에서는 super 키워드를 사용하여 상위 클래스의 생성자를 호출한다.

```kotlin
1. 보조 생성자가 기본 생성자를 호출하여 로직을 재사용
class Person(val name: String, val age: Int) {
    constructor(name: String) : this(name, 0) // 기본 생성자 호출
}

2. 보조 생성자 간 위임
class Person {
    var name: String
    var age: Int

    constructor(name: String) : this(name, 0) // 두 번째 보조 생성자 호출
    constructor(name: String, age: Int) {
        this.name = name
        this.age = age
    }
}

3. 상위클레스 생성자 호출
open class Parent(val name: String)

class Child(name: String, val age: Int) : Parent(name) {
    init {
        println("Child created: $name, $age years old")
    }
}
```

#### 4.2.3 인터페이스에 선언된 프로퍼티 구현
- 인터페이스에 프로퍼티를 선언하면 구현 클래스에서 프로퍼티를 오버라이드 해야한다.
- 인터페이스에 프로퍼티를 선언하면 getter/setter를 포함한 프로퍼티를 선언할 수 있다.

```kotlin
interface User {
    val nickname: String
}

class PrivateUser(override val nickname: String) : User
class SubscribingUser(val email: String) : User {
    override val nickname: String
        get() = email.substringBefore('@')
}

interface User {
    val email: String
    val nickname: String
        get() = email.substringBefore('@') // 매번 결과를 계산해서 돌려주는 방식
  
}
```

### 4.2.4 게터와 세터에서 뒷받침하는 필드 접근
- 키워드 `field` 를 통해서 프로퍼티의 값을 저장할 필드를 참조할 수 있다.
- 게터에서는 field를 읽은수만 있고, setter에서는 field를 읽고 쓸 수 있다.

```kotlin
class User(val name: String) {
    var address: String = "test"
        set(value: String) {
            println("""
                Address was changed for $name: "$field" -> "$value".
            """.trimIndent())
            field = value
        }
}
```

### 4.2.5 접근자의 가시성 변경
- 프로퍼티의 접근자에 가시성 변경자를 사용하여 접근을 제한할 수 있다.

```kotlin
class LengthCounter {
    var counter: Int = 0
        private set // setter의 가시성을 private으로 변경

    fun addWord(word: String) {
        counter += word.length
    }
}

val lengthCounter = LengthCounter()
    
lengthCounter.addWord("Hi!")
println(lengthCounter.counter) // 3
lengthCounter.counter = 4 // ❌ Error: setter가 private이므로 접근 불가
```



### 4.3 컴파일러가 생성한 메서드: 데이터 클래스와 클래스 위임
#### 4.3.1 모든 클래스가 정의해야하는 메서드
- toString() : 인스턴스의 문자열 표현을 얻을때 
- equals() (동등성) : 두 객체가 같은지 비교할때, 코틀린에서는 == 연산자가 내부적으로 equals를 비교한다.
  📌 참조 비교의 경우, === 를 사용한다.
- hashCode() : 해시맵과 같은 해시기반 컬렉션에서 객체를 저장할때 equals와 함께 사용한다.

#### 4.3.2 데이터 클래스: 모든 클래스가 정의해야 하는 메서드 자동 생성
- data클래스의 경우 toString(), equals(), hashCode() 메서드를 정의하지 않아도 컴파일러가 자동으로 생성해준다.
- data클래스는 copy() 메서드를 제공하여 객체를 복사할 수 있다.

```kotlin
data class Client(val name: String, val postalCode: Int)
 
val client1 = Client("Alice", 342562)
val client2 = Client("Alice", 342562)
println(client1 == client2) // true
```

- data클래스는 복사를 통해 객체를 생성할때 일부 프로퍼티를 변경할 수 있다.

```kotlin

val bob = Client("Bob", 973293)
println(bob.copy(postalCode = 382555)) // Client(name=Bob, postalCode=382555)
```

#### 4.3.3 클래스 위임: by 키워드 사용
- 데코레이터 패턴 : 상속을 허용하지 않는 클래스 대신 사용할 수 있는 새로운 클래스를 만들되, 기존클래스와 같은 인터페이스를 데코레이터가 제공한다. 그리고 기존 클래스를 데코레이터 내부에 필드로 유지한다.
- by 키워드를 사용하여 클래스 위임을 사용할 수 있다.

```kotlin
class DelegateCollection<T>(
    innerList: Collection<T> = ArrayList<T>()
) : Collection<T> by innerList // Collection의 구현을 innerList에게 위임한다.

val delegateCollection = DelegateCollection(listOf(1, 2, 3))
println(delegateCollection.size) // 3
```
- override를 통하여 특정 메서드만 재정의하여 사용도 가능한다.

### 4.4 object 키워드: 클래스 선언과 인스턴스 생성
- object 키워드를 사용하여 클래스 선언과 인스턴스 생성을 동시에 할 수 있다.
- object 키워드를 사용하여 싱글턴 객체를 생성할 수 있다.
- 동반 객체(companion object): 인스턴스 메서드는 아니지만, 클래스 내부에 선언되어 클래스 이름을 통해 직접 접근한다.
  - 클래스당 **하나의 동반 객체**만 가질 수 있습니다.
  - **클래스 이름을 통해 직접 접근**할 수 있으며, **별도의 인스턴스 생성이 필요하지 않습니다.**
  - 동반 객체는 **클래스와 연결된 싱글톤 객체**로 동작합니다.
  - **인터페이스를 구현**할 수 있습니

### 4.4.1 객체 선언: 싱글턴을 쉽게 만들기
- object 키워드를 사용하여 싱글턴 객체를 생성할 수 있다. 
- 즉시 생성되기에 생성자 정의없이 가능하다.

```kotlin
object Payroll {
    val allEmployees = arrayListOf<Person>()
    fun calculateSalary() {
        for (person in allEmployees) {
            println("Salary for ${person.name}")
        }
    }
}

fun main() {
    Payroll.allEmployees.add(Person("Alice"))
    Payroll.calculateSalary()
}
```

- 객체 선언도 클래스나 인터페이스를 상속 가능하다.
- 예를 들어 Comparator같이 데이터를 저장할 필요없이 객체를 비교하는경우 유용하다.
```kotlin
data class Person(val name: String, val age: Int)

object PersonNameLengthComparator : Comparator<Person> {
    override fun compare(p1: Person, p2: Person): Int { // 상태값은 존재하지 않는다.
        return p1.name.length - p2.name.length
    }
}

fun main() {
    val people = listOf(
        Person("Alice", 25),
        Person("Bob", 30),
        Person("Charlotte", 22)
    )

    val sortedByNameLength = people.sortedWith(PersonNameLengthComparator)
    println(sortedByNameLength)
}

// 클래스안에서 객체를 선언할 수 있다. 이경우도 인스턴스는 하나다.
data class Person(val name: String, val age: Int) {
    object NameComparator : Comparator<Person> {
        override fun compare(p1: Person, p2: Person): Int {
            return p1.name.compareTo(p2.name)
        }
    }
}
```

### 4.4.2 동반 객체: 팩토리 메서드와 정적 멤버가 들어갈 장소
- 코틀린 언어는 자바의 static을 지원하지 않는다. 대신 패키지 수준의 최상위 함수와 객체선언을 사용한다.
- 클래스 내부에 접근해야하는 경우, 동반 객체를 사용한다.

```kotlin
class A {
    companion object {
        fun bar() {
            println("Companion object called")
        }
    }
}

fun main() {
    A.bar()
}
```

- 동반객체를 통해 바깥 private 생성자를 호출할 수 있다. (팩토리 메서드 패턴)
```kotlin
class A private constructor(val name: String) {
    companion object {
        fun create(name: String): A {
            return A(name)
        }
    }
}

fun main() {
    val a = A.create("Alice")
    println(a.name) // Alice
}
```

### 4.4.3 동반 객체를 일반 객체처럼 사용
- 동반객채 또한 일반 객체처럼 사용이 가능하다.
- 동반객체는 인터페이스를 구현할 수 있다.

```kotlin
class Person(val name: String) {
    companion object Loader {
        fun fromJSON(jsonText: String): Person {
            return Person(jsonText)
        }
    }
}

fun main() {
    val person = Person.Loader.fromJSON("{name: 'Alice'}")
    println(person.name) // Alice
  
    val person2 = Person.fromJSON("{name: 'Alice'}") // 이렇게도 호출이 된다.
    println(person2.name) // Alice
}

// 인터페이스 사용 예시
interface JsonFactory<T> { 
    fun fromJson(jsonText: String): T
}
class Person(val name: String) { 
    companion object : JSONFactory<Person> { 
        override fun fromJson(jsonText: String): Person = ... // 동반 객체에서 인터페이스를 구현한다.
    }
}

```
📌 자바에서는 Companion 이라는 이름을 붙여서 참조에 접근할 수 있다. <br />
코틀린에서 @JvmStatic 어노테이션을 사용하여 동반 객체의 메서드를 정적 메서드로 사용할 수 있다.
```java
Person.Companion.fromJSON("{name: 'Alice'}");
```

#### 동반 객체 확장
- 동반객체에 대한 확장함수를 작성하려면 비어있더라도 동반객체를 꼭 선언해야 한다.
```kotlin
// 비어있는 동반 객체 선언
class Person(val firstName: String, val lastName: String) {
	companion object {
	}
}

// 확장 함수 선언
fun Person.Companion.fromJson(json:String): Person {
	...
}

// 사용
val person = Person.fromJson(json)
```

### 4.4.4 객체 식: 무명 내부 클래스 대신 객체 선언 사용
- 자바의 무명 내부클래스를 선언할때도 object 키워드를 사용할 수 있다.
- 객체식은 무명 객체를 생성할때 사용한다.

```kotlin
window.addMouseListener(object : MouseAdapter() {
    override fun mouseClicked(e: MouseEvent) {
        // ...
    }

    override fun mouseEntered(e: MouseEvent) {
        // ...
    }
})

// 객체에 이름을 붙여야한다면, 변수를 사용한다.
val listener = object : MouseAdapter() {
  override fun mouseClicked(e: MouseEvent) {}
  ..
}
```
- 객체선언과 달리 무명객체는 싱글턴이 아니다.

