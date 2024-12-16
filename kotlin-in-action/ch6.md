# 6 코틀린 타입 시스템

### 6.1 널 가능성
- 널이 될 수 있는  타입에 대해 알아본다. 코틀린에서 null을 어떻게 처리하는지 자바와 어떻게 함께 사용하는지 살펴본다.



#### 6.1.1 널이 될 수 있는 타입
- 코틀린의 null이거나 null이 될 수 있는 것을 컴파일시점에 감지한다. (100%는 아니다)

```kotlin
fun strLen(s: String) = s.length

fun main() {
    println(strLen("abc")) // 3
    println(strLen(null)) // 컴파일 에러
}
```

- 위 코드는 컴파일 에러가 발생한다. null이 될 수 있는 타입을 사용하려면 타입 이름 뒤에 ?를 붙여야한다.

```kotlin
fun strLen(s: String?) = s.length
```

- null이 될 수 있는 타입을 null이 될 수 없는 변수에 대입할 수 없다.
- 널이 될 수 있는 타입의 값을 널이 될 수 없는 타입의 파라미터를 받는 함수에 전달할 수 없다.
- null에 대한 검사를 추가하면 컴파일 에러를 피할 수 있다.

```kotlin
val x: String? = null
val y: String = x // 컴파일 에러

fun strLen(s: String) = s.length
strLen(x) // 컴파일 에러, Type mismatch: inferred type is String? but String was expected


fun strLenSafe(s: String?) = if (s != null) s.length else 0

fun main() {
    println(strLenSafe("abc")) // 3
    println(strLenSafe(null)) // 0
}
```

#### 6.1.3 안전한 호출 연산자: ?.
- null 을 검사하는 유용한 도구중 하나로 ?.가 사용된다.
- 아래의 두 식은 동일하다.
```kotlin
// 1.
s?.toUpperCase()

// 2.
if(s != null){
    s.toUpperCase()
} else{
    null
}
```

- ?.는 null이 될 수 있는 프로퍼티나 메서드 호출을 안전하게 수행할 수 있다.
```kotlin
class Employee(val name: String, val manager: Employee?)

fun managerName(employee: Employee): String? = employee.manager?.name

val ceo = Employee("Da Boss", null)
val devepoler = Employee("Bob Smith", ceo)
println(managerName(ceo)) // null
println(managerName(developer)) // "Da Boss"
```

- ?.는 체인을 이어갈 수 있다.
```kotlin
class Address(val streetAddress: String, val zipCode: Int, val city: String, val country: String)
class Company(val name: String, val address: Address?)
class Person(val name: String, val company: Company?)

fun Person.countryName() : String {
    val country = this.company?.address?.country
    return if(country != null) country else "Unknown"
}

val person = Person("Dmitry", null)
println(person.countryName())
// Unknown
```

#### 6.1.4 엘비스 연산자: ?:
- 엘비스 연산자는 null이 아닌 경우에는 그 값을 반환하고, null인 경우에는 우측의 값을 반환한다.
```kotlin
fun strLenSafe(s: String?): Int = s?.length ?: 0

fun main() {
    println(strLenSafe("abc")) // 3
    println(strLenSafe(null)) // 0
}

// countryName() 함수를 엘비스 연산자로 변경
fun Person.countryName() = company?.address?.country ?: "Unknown"
```

아래는 엘비스 연산자와 throw를 함께 사용하는 코드이다.
```kotlin
class Address(val streetAddress: String, val zipCode: Int, val city: String, val country: String)
class Company(val name: String, val address: Address?)
class Person(val name: String, val company: Company?)

fun prinltnShippingLabel(person: Person) {
    val address = person.company?.address ?: throw IllegalArgumentException("No address")
    with(address) {
        println(streetAddress)
        println("$zipCode $city, $country")
    }
}

printShippingLabel(Person("Dmitry", Company("JetBrains", Address("Elsestr. 47", 2323, "dsasd", "test"))))
```

#### 6.1.5 안전한 캐스트: as?
- as? 연산자는 캐스트를 시도하고, 캐스트에 실패하면 null을 반환한다.
```kotlin
fun main() {
    val obj: Any = "Hello, Kotlin!"

    // 안전한 캐스트를 시도하고, null일 경우 기본값을 설정
    val str: String = obj as? String ?: "Default String"
    println(str)  // 출력: Hello, Kotlin!

    val numObj: Any = 123

    // 안전한 캐스트를 시도하고, null일 경우 기본값을 설정
    val strNum: String = numObj as? String ?: "Fallback String"
    println(strNum)  // 출력: Fallback String
}
```

#### 6.1.6 널 아님 단언 연산자: !!
- !! 연산자는 null이 될 수 있는 값을 널이 될 수 없는 타입으로 변환한다.
- null이 될 수 있는 값을 널이 될 수 없는 타입으로 변환할 때, null이면 NPE를 발생시킨다.
```kotlin
fun ignoreNulls(s: String?) {
    val sNotNull: String = s!!
    println(sNotNull.length)
}

fun main() {
    ignoreNulls("abc") // 3
    ignoreNulls(null) // NPE 발생
}
```
- 컴파일러를 무시하고 단언하는 선언이다, 더 나은 방법을 찾아 사용하는 것이 좋다. (하지만 현실은 아니지)
- !! 사용해서 NullPointException이 발생한 경우, 한 라인에 여러 !! 를 사용하는경우, 어디서 발생했는지 확인이 어려우니 한줄에 몰아쓰는걸 지양해야한다.

#### 6.1.7 let 함수
- let 함수는 null이 될 수 있는 값을 다룰 때 유용하다.
- 변수에 별도로 지정할 필요없이, let 함수를 사용하면 null이 될 수 있는 값을 다룰 수 있다.
```kotlin
fun sendEmailTo(email: String) {
    println("Sending email to $email")
}

fun main() {
    var email: String? = "test@gmail.com"
    email?.let { sendEmailTo(it) }
}

// let 변수에 따로 지정할 필요가 없다.
fun main() {
    val obj: Any = "Hello, Kotlin!"

    // as? 와 let을 사용해 null을 다룸
    (obj as? String)?.let {
        println("Casting successful: $it") // 출력: Casting successful: Hello, Kotlin!
    } ?: run {
        println("Casting failed.") // 실행되지 않음
    }

    val anotherObj: Any = 123

    // 캐스팅 실패시 대체 동작 실행
    (anotherObj as? String)?.let {
        println("Casting successful: $it") // 실행되지 않음
    } ?: run {
        println("Casting failed.") // 출력: Casting failed.
    }
}
```

#### 6.1.8 나중에 초기화할 프로퍼티
- 나중에 초기화할 프로퍼티는 lateinit 키워드를 사용한다.
- lateinit 키워드는 var 프로퍼티에만 사용할 수 있다. 또한 null이 될 수 없는 타입이어야 한다.
- lateinit 프로퍼티는 초기화되지 않은 상태에서 사용하면 예외가 발생한다. "lateinit property name has not been initialized"

```kotlin
class Example {
    lateinit var text: String // lateinit으로 선언

    fun initialize() {
        text = "Hello, Kotlin!"
    }

    fun printText() {
        if (::text.isInitialized) { // 초기화 여부 확인
            println(text)
        } else {
            println("text is not initialized")
        }
    }
}

fun main() {
    val example = Example()

    // 초기화되지 않은 상태에서 접근 시도
    example.printText() // 출력: text is not initialized

    // 초기화 후 접근
    example.initialize()
    example.printText() // 출력: Hello, Kotlin!
}
```

#### 6.1.9 널이 될 수 있는 타입 확장
- 널이 될 수 있는 타입에 대해 확장 함수를 선언할 수 있다, 확장 함수를 선언하면, null이 될 수 있는 값을 다룰 때 유용하다.
```kotlin
fun verifyUserInput(input: String?) {
    if (input.isNullOrBlank()) {
        println("Please fill in the required fields")
    }
}

fun main() {
    verifyUserInput(" ") // 출력: Please fill in the required fields
    verifyUserInput(null) // 출력: Please fill in the required fields
}

    
fun String?.isNullOrBlank(): Boolean {
    return this == null || this.isBlank()
}

fun main() {
    val empty: String? = null
    println(empty.isNullOrBlank()) // true

    val blank: String? = " "
    println(blank.isNullOrBlank()) // true

    val valid: String? = "abc"
    println(valid.isNullOrBlank()) // false
}
```

### 6.1.10 타입 파리미터의 널 가능성
- 타입 파라미터에 대한 널 가능성을 지정할 수 있다.
- 타입 파라미터에 대한 널 가능성을 지정하려면 타입 파라미터 이름 뒤에 ?를 붙인다.
```kotlin
fun <T> printHashCode(t: T) {
    println(t?.hashCode())
}

fun main() {
    printHashCode(null) // 출력: null
    printHashCode(42) // 출력: 42
}
```

- 타입상한을 지정하면 null을 막을 수 있다.
```kotlin
fun <T: Any> printHashCode(t: T) {
    println(t.hashCode())
}

fun main() {
    printHashCode(null) // 컴파일 에러
    printHashCode(42) // 출력: 42
}
```

#### 6.1.11 널 가능성과 자바
- 플랫폼 타입 : 코틀린이 널 관련 정보를 알 수 없는 타입을 말한다. 즉 컴파일러는 ?를 써도 안써도 모두 허용한다.

```java
public class JavaFile {
    public static String getNonNullString() {
        return "Hello from Java!";
    }

    public static String getNullableString() {
        return null; // 의도적으로 null 반환
    }
}
```
```kotlin
fun main() {
    val nonNull: String = JavaFile.getNonNullString()
    println(nonNull) // 출력: Hello from Java!

    val nullable: String? = JavaFile.getNullableString()
    println(nullable) // 출력: null

    val length = nullable.length // 컴파일 에러
}
```
- 플랫폼 타입의 모호성을 제거하려면 명시적 캐스팅 또는 Kotlin의 null 안전 연산자를 활용 
 ```kotlin
  fun main() {
    // Nullable로 명시적으로 선언
    val nullableString: String? = JavaFile.getNullableString()

    // null 검사를 통해 안전하게 처리
    val result = nullableString ?: "Default Value"
    println(result) // 출력: Default Value
}
```

### 6.2 코틀린의 원시 타입

#### 6.2.1 Int Boolean 등 
- 코틀린은 원시 타입을 제공하지 않으므로 항상 같은 타입을 사용한다.

```kotlin
val list: List<Int> = listOf(1, 2, 3)
val map: Map<Int, String> = mapOf(1 to "one", 2 to "two", 3 to "three")
```
- 코틀린은 실행시점에 가장 효율적인 방식으로 표현한다. Int의 경우 java 의 int로 컴파일된다. 컬렉션의 파라미터로 넘기면 Integer로 박싱된다.

#### 6.2.2 널이 될 수 있는 원시 타입 : Int? Boolean? 등
- 널이 가능한 케이스의 경우 자동으로 자바의 래퍼타입으로 컴파일된다.
- 제네릭 클래스의 경우, 래퍼타입을 사용한다.

#### 6.2.3 숫자변환 
- 코틀린은 숫자를 자동으로 변환하지 않는다. 명시적으로 변환해야한다.
```kotlin
val i = 1
val l: Long = i.toLong()
```

- 코틀린은 숫자를 넓은 타입으로 자동으로 변환하지 않는다.
```kotlin
val x = 1
val list = listOf(1L, 2L, 3L)
println(x in list) // 컴파일 에러

println(x.toLong() in list) // true
```


#### 6.2.4 Any, Any?: 최상위 타입
- Any는 모든 타입의 조상이다. (null 제외) Any?는 널이 될 수 있는 모든 타입의 조상이다. 구분이 되는 object라 보면 된다.

```kotlin
val answer: Any = 42
```

#### 6.2.5 Unit 타입: 코틀린의 void
- Unit은 자바의 void와 같다. 반환값이 없는 함수의 경우 Unit을 반환한다.
- Unit을 반환하는 함수는 반환 타입을 생략할 수 있다.
```kotlin
fun f(): Unit {
    println("f")
}

fun f() {
    println("f")
}
```


- java의 void
정의: void는 아무 값도 반환하지 않는 메서드를 의미
- 특징: 
  - 타입이 아님: void는 리턴 타입이 없는 것을 나타내는 키워드. 실제 값도 존재하지 않는다.
  - 표현 불가능: void 자체는 객체로 다룰 수 없으며, 변수에 저장하거나 다룰 수 없다
  - ```kotlin
    public void printMessage() {
        System.out.println("Hello, Java!");
    }
    ```

- kotlin의 Unit\
정의: Unit은 아무 값도 반환하지 않는 함수를 나타내는 타입
- 특징:
  - 타입: Unit은 객체이며, 실제로는 Unit 객체를 반환한다. 고차함수(뒤에서 설명)에서 직접 사용가능
  - 표현 가능: Unit은 객체이므로 변수에 저장하거나 다룰 수 있다.
  - ```kotlin
    fun printMessage() {
        println("Hello, Kotlin!")
    }
    ```
| 특징                      | Java `void`                  | Kotlin `Unit`                  |
|---------------------------|------------------------------|--------------------------------|
| **개념**                  | 반환값이 없는 상태           | 반환값이 없는 타입             |
| **타입 여부**             | 타입이 아님                  | `Unit`은 실제 타입(객체)       |
| **값 여부**               | 값이 없음                   | `Unit` 객체가 반환됨          |
| **명시적 반환**           | 없음                        | 가능 (`return Unit`)          |
| **고차 함수 활용**         | 사용 불가                   | 가능 (예: 반환 타입으로 사용)  |
| **함수 표현식 사용**       | 지원하지 않음               | 지원 (암시적 `Unit` 반환)      |


#### 6.2.6 Nothing 타입: 이 함수는 결코 정상적으로 끝나지 않는다.
- 값을 가질 수 없음: Nothing은 함수가 정상적으로 값을 반환하지 않는다는 것을 명시적으로 표현
- 예외 처리: 주로 예외를 던지는 함수에서 사용
```kotlin
fun fail(message: String): Nothing {
    throw IllegalArgumentException(message)
}

fun main() {
    fail("This is an error") // 예외 발생 후 프로그램 종료
}

 ```

- Nothing? 타입: null 이외의 값을 절대 가질 수 없는 타입으로 활용
```kotlin
val value: Nothing? = null // null 외의 값을 가질 수 없음
```

- 타입시스템을 강화하는데 도움을 준다. 타입추론이 명확해지고 불필요한 오류를 방지
```kotlin
fun getStringOrFail(input: String?): String {
    return input ?: fail("Input is null") // fail()이 Nothing을 반환하므로 타입 충돌 없음
}

fun main() {
    println(getStringOrFail("Kotlin")) // 출력: Kotlin
    println(getStringOrFail(null))    // 예외 발생: IllegalArgumentException
}

```

### 6.3 컬렉션과 배열

- 배열까지 참고차 같이 기입

| 컬렉션 타입   | 생성 함수                  | 설명                                            | 예시                          |
|---------------|---------------------------|------------------------------------------------|--------------------------------|
| **List**      | `listOf()`                | 불변 리스트를 생성합니다.                        | `val list = listOf(1, 2, 3)`   |
|               | `mutableListOf()`         | 가변 리스트를 생성합니다.                        | `val mList = mutableListOf(1)` |
|               | `arrayListOf()`           | `ArrayList`를 생성합니다.                        | `val aList = arrayListOf(1)`   |
| **Set**       | `setOf()`                 | 불변 집합(Set)을 생성합니다.                     | `val set = setOf(1, 2, 3)`     |
|               | `mutableSetOf()`          | 가변 집합(Set)을 생성합니다.                     | `val mSet = mutableSetOf(1)`   |
|               | `hashSetOf()`             | `HashSet`를 생성합니다.                          | `val hSet = hashSetOf(1, 2)`   |
| **Map**       | `mapOf()`                 | 불변 맵(Map)을 생성합니다.                       | `val map = mapOf("a" to 1)`    |
|               | `mutableMapOf()`          | 가변 맵(Map)을 생성합니다.                       | `val mMap = mutableMapOf(1)`   |
|               | `hashMapOf()`             | `HashMap`를 생성합니다.                          | `val hMap = hashMapOf(1 to 2)` |
| **특수 컬렉션** | `emptyList()`             | 비어있는 불변 리스트를 생성합니다.                | `val list = emptyList<Int>()`  |
|               | `emptySet()`              | 비어있는 불변 집합(Set)을 생성합니다.             | `val set = emptySet<Int>()`    |
|               | `emptyMap()`              | 비어있는 불변 맵(Map)을 생성합니다.               | `val map = emptyMap<Int, Int>()` |
| **배열**       | `arrayOf()`               | 배열을 생성합니다.                               | `val arr = arrayOf(1, 2, 3)`   |
|               | `intArrayOf()`            | `IntArray`를 생성합니다.                         | `val iArr = intArrayOf(1, 2)`  |
|               | `listOfNotNull()`         | `null`이 아닌 요소만 포함된 리스트를 생성합니다.  | `val list = listOfNotNull(1, null, 2)` |

- Kotlin 1.0 ~ 1.1 초기 버전 :  자바와 같이 사용하는경우, 자바메서드에 읽기전용 컬렉션을 넘겨도 코틀린 컴파일러가 이를 막을 수는 없다.
- Kotlin 1.1 이후 : Kotlin 1.1부터 listOf(), setOf(), mapOf()는 런타임 불변성을 보장하기 위해 Java의 Collections.unmodifiableList 등으로 감싸도록 개선

```kotlin
fun main() {
    val kotlinList: List<Int> = listOf(1, 2, 3) // 읽기 전용 리스트
    JavaUtil.modifyList(kotlinList)            // Java 메서드 호출
    println(kotlinList)                        // 출력 결과 확인
}
```

```java
import java.util.List;

public class JavaUtil {
    public static void modifyList(List<Integer> list) {
        list.add(4); // Kotlin의 읽기 전용 List도 Java에서는 add가 가능!
    }
}
```

- 위 결과는 코틀린 버전에 따라 다르게 동작한다.


#### 6.3.4 컬렉션을 플렛폼 타입으로 다루기
- Kotlin에서는 Java 코드에서 넘어온 컬렉션을 플랫폼 타입으로 다룬다. nullable 여부를 명확하게 하는게 좋다.

```java
import java.util.List;

public class JavaUtil {
  public static List<String> getStrings() {
    return null; // 또는 실제 List<String> 반환
  }
}
```
```kotlin
fun main() {
    val list = JavaUtil.getStrings() // 플랫폼 타입: List<String!>
    println(list?.size) // Nullable처럼 다룰 수 있음
    println(list.size)  // Non-null처럼 다룰 수도 있음 (NullPointerException 가능)
}
```
- String!은 Kotlin 컴파일러가 null 가능성을 추론할 수 없는 상태

#### 6.3.5 객체의 배열과 원시 타입의 배열

| **구분**             | **객체 배열 (Array<T>)**            | **원시 타입 배열 (IntArray 등)**       |
|-----------------------|-------------------------------------|----------------------------------------|
| **생성 방식**         | `arrayOf()`                         | `intArrayOf()`, `floatArrayOf()`       |
| **JVM 내부 표현**     | `Object[]`                         | `int[]`, `float[]` 등                  |
| **메모리 사용량**     | 큼 (참조 저장)                     | 작음 (원시 값 저장)                    |
| **성능**              | 상대적으로 느림                    | 빠름                                  |
| **사용 예시**         | `Array<Int>`                       | `IntArray`, `FloatArray`               |

- 컬렉션과 호환성이 좋다
```kotlin
fun main() {
    val array = arrayOf(1, 2, 3)
    val list = array.toList() // 배열을 리스트로 변환, 읽기전용이다.
    println(list) // 출력: [1, 2, 3]
}


fun main() {
  val array = arrayOf(1, 2, 3)
  val mutableList = array.toMutableList()
  mutableList.add(4) // 리스트에 요소 추가 가능
  println(mutableList) // 출력: [1, 2, 3, 4]
}

// 컬렉션에서 배열로
fun main() {
  val list = listOf(1, 2, 3)
  val array = list.toTypedArray() // 리스트를 배열로 변환
  println(array.joinToString()) // 출력: 1, 2, 3
}

// toIntArray(), toDoubleArray()와 같은 메서드로 특정 원시 타입 배열로 변환가능
fun main() {
  val list = listOf(1, 2, 3)
  val intArray = list.toIntArray() // 리스트를 IntArray로 변환
  println(intArray.joinToString()) // 출력: 1, 2, 3
}


```
