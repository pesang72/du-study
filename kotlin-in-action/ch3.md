# 3 함수정의와 호출

### 3.1 코틀린에서 컬렉션 만들기
코틀린 컬렉션 생성하는법 
```kotlin
val set = hashSetOf(1, 7, 53)
val list = arrayListOf(1, 7, 53)
val map = hashMapOf("key" to 1, "foo" to 7)

println(set.javaClass) // class java.util.HashSet
println(list.javaClass) // class java.util.ArrayList
println(map.javaClass) // class java.util.HashMap
```

코틀린은 자신만의 컬렉션을 제공하지않는다. (자바 컬렉션을 사용한다.) <br />
- 코틀린에서 자바 컬렉션을 사용하면 상호작용하기가 더 쉽다
- 코틀린에서는 자바보다 더 많은 기능을 사용할 수 있다.

```kotlin
val strings = listOf("first", "second", "fourteenth")
println(strings.last()) // fourteenth

val numbers = setOf(1, 14, 2)
println(numbers.max())

// java라면
Set<Integer> numbers = Set.of(1, 14, 2);
System.out.println(numbers.stream().max(Integer::compareTo).orElseThrow());
System.out.println(Collections.max(numbers));
```


### 3.2 함수를 호출하기 쉽게 만들기
- 코틀린에서는 함수를 호출할 때 인자 이름을 명시할 수 있다. (순서를 변경가능)
- 호출시 인자 중 어느 하나도로 이름을 명시하고 나면, 혼동을 막기위해 **그 뒤에오는 모든 인자**는 이름을 명시해야한다.
```kotlin
fun main() {
    println(test(notWork = 3, plus1 = 2, minus = 1)) // 1
}
fun test(minus : Int, plus1 : Int, notWork: Int) : Int {
    return 0 + plus1- minus
}
```

#### 디폴트 파라미터 값
- 자바에서는 일부 클래스에서 오버로딩한 메서드가 너무 많아지는 문제가 있다 (디폴트값을 적용하기 위해)
- ex Java에서 LocalDateTime.of를 생각해보자
  - LocalDateTime.of(2021, 1, 1, 0, 0, 0 -> 6개의 오버로딩된 메서드 (초단위까지)
  - LocalDateTime.of(2021, 1, 1, 0, 0) -> 5개의 오버로딩된 메서드 (분단위까지)

- 코틀린에서는 디폴트 파라미터 값을 사용할 수 있다. (호출하는 쪽이 아닌 함수에서 사용)
- 디폴트 파라미터 값은 함수 호출시 모든 인자를 쓸수도 있고 일부 인자를 생략할 수 있다.
```kotlin
fun test(minus : Int, plus1 : Int = 100, printNum: Int = 50) : Int {
    println(printNum)
    return 0 + plus1- minus
}

fun main() {
    println(test(1)) // 50, 99
    println(test(1, 2)) // 50, 1
    println(test(1, 2, 3)) // 3, 1
    println(test(1, printNum = 3)) // 3, 99
}
```
- 마지막 println처럼 중간에 있는 인자를 생략하고, 지정하고 싶은 인자를 이름을 붙여서 순서와 상관없이 지정할 수 있다
<br /> 

> 디폴트 값과 자바
> 자바에서는 디폴트 값이라는 걔념이 없기에 코틀린 함수를 자바에서 호출하는경우, 모든 인자를 명시해야한다.
> 
> @JvmOverloads 어노테이션을 사용하면 코틀린 컴파일러가 자동으로 맨 마지막 파라미터로부터 파라미터를 하나씩 생략한 오버로딩 자바 메서드를 추가해준다.
> 


### 3.2.3 정적인 유틸리티 클래스 없애기: 최상위 함수와 프로퍼티
- 코틀린에서는 정적 메서드를 가진 클래스가 필요없다. (자바에서는 유틸리티 클래스를 만들어서 사용)
- 코틀린에서는 최상위 함수를 사용할 수 있다.
```kotlin
package strings

fun joinToString(...): String { ... }


/* 자바에서 호출하는 경우 */
import strings.JoinKt;

JoinKt.joinToString(...);
```
- 컴파일러는 이 파일을 컴파일할떄, 새로운 클래스를 정의해준다.
- Jvm언어에서 호출하는 경우, 컴파일시점에 최상위 함수가 ㄷ르어있던 코틀린 소스파일의 이름과 대응한다.
- 만약 위 파일이 JoinKt라는 이름으로 컴파일되었다면, 자바에서는 JoinKt.joinToString()으로 호출할 수 있다.


#### 최상위 프로퍼티
- 코틀린에서 클래스 밖에 선언된 프로퍼티로, 파일 수준에서 정의된다.
- 일반적인 프로퍼티와 동일하게 getter 메서드를 통해 접근할 수 있다.
  - val로 선언된 경우 읽기 전용 getter 생성.
  - var로 선언된 경우 읽기 쓰기 가능한 getter, setter 생성.
- 최상위 프로퍼티는 해당 파일 이름을 기반으로 한 클래스로 컴파일됩니다. 예를 들어, example.kt 파일에 정의된 최상위 프로퍼티는 ExampleKt라는 이름의 클래스에 포함됩니다.
- 상수를 정의하려면 const val을 사용합니다.이는 컴파일 시 자바의 public static final 필드로 변환됩니다. (상수를 사용할 때는 const val을 사용하자 (getter는 부자연스럽다))
```kotlin
const val VERSION = "1.0"
val name = "Kotlin"

/** 자바코드 */
public class ExampleKt {
  public static final String VERSION = "1.0";
  
  private static final String name = "Kotlin";
  public static final String getName() {
    return "Kotlin";
  }
}
```

### 3.3 메서드를 다른 클레스에 추가: 확장 함수와 확장 프로퍼티

- 어떤 클래스의 멤버 메서드인 것처럼 호출할 수 있는 함수를 **확장 함수**라고 한다.
- 확장 함수는 클래스의 일부가 아니라 클래스 밖에서 정의된 함수이다. (실제 그 클래스 안에 선언되지않는다.)
- 확장 함수를 사용하면 기존 클래스에 메서드를 추가할 수 있다.
```kotlin
fun String.lastChar() : Char = this.get(this.length - 1)
println("Kotlin".lastChar()) // n
```

- 확장함수가 this를 사용하면서 메서드나 프로퍼티를 바로 사용할 수 있다.
- 그러나 확장 함수는 클래스의 private, protected 멤버에 접근할 수 없다.

### 3.3.1 임포트와 확장 함수
- 확장 함수를 사용하려면 해당 함수가 정의된 패키지를 임포트해야한다.
- as 키워드를 통하여 이름을 변경할 수 있다.
```kotlin
import strings.lastChar as last
println("Kotlin".last()) // n
```

### 3.3.2 자바에서 확장함수 호출
- 코틀린에서 확장함수를 호출하는 경우, 자바에서는 정적 메서드로 호출한다.
- 확장 함수는 정적 메서드로 컴파일되기 때문에, 자바에서는 정적 메서드로 호출한다.
```kotlin
fun String.lastChar() : Char = this.get(this.length - 1)
```
```java
char c = StringUtilKt.lastChar("Kotlin");
```
- 자바에서 확장 함수를 호출할 때는 해당 함수가 정의된 클래스 이름에 Kt를 붙여야한다.


### 3.3.3 확장 함수로 유틸리티 함수 정의
```kotlin
fun <T> Collection<T>.joinToString(
        separator: String = ", ",
        prefix: String = "",
        postfix: String = ""
): String {
    val result = StringBuilder(prefix)

    for ((index, element) in this.withIndex()) {
        if (index > 0) result.append(separator)
        result.append(element)
    }

    result.append(postfix)
    return result.toString()
}

>>> val list = listOf(1, 2, 3)
>>> println(list.joinToString(separator = "; ", prefix = "(", postfix = ")"))
(1; 2; 3;)
```
- 컬렉션에 대한 확장함수의 예시
- 확장 함수는 단시 정적 메서드 호출에 대한 문법적인 편의일 뿐(클래스가 아닌 더 구체적인 타입을 수신객체로 지정가능)

### 3.3.4 확장 함수는 로버라이드 할 수 없다.
- 확장함수는 클래스의 일부가 아니다. 클래스 밖에 선언되기에 동적인 타입에 의해 확장함수가 결정되지 않는다.
```kotlin
fun View.showOff() = println("I'm a view!")
fun Button.showOff() = println("I'm a button!")

val view: View = Button()
view.showOff() // I'm a view!
```
- Button을 인스턴스로 가지고 있으나, 실제로는 View의 확장함수가 호출된다.
> 어떤 클래스를 확장한 함수와 그 클래스 맴버함수의 이름과 시그니처가 같다면 맴버 함수가 우선순위가 높다 <br />

<br />

### 3.3.5 확장 프로퍼티
```kotlin
val String.lastChar: Char
    get() = get(length - 1)
```
- 변경이 불가능한 확장 프로퍼티 선언

```kotlin
var StringBuilder.lastChar: Char
    get() = get(length - 1)
    set(value: Char) {
        this.setCharAt(length - 1, value)
    }


val sb = StringBuilder("Kotlin?")
sb.lastChar = '!'
println(sb) // Kotlin!
```
- 변경이 가능한 확장 프로퍼티 선언

### 3.4 컬렉션 처리: 가변 길이 인자, 중위 호출 구문
- vararg 키워드: java의 ...을 대신한다.
```kotlin
fun printNumbers(vararg numbers: Int) {
    for (number in numbers) {
        println(number)
    }
}

fun main() {
    // 여러 개의 인자를 전달
    printNumbers(1, 2, 3, 4, 5)

    // 배열을 전달할 때는 스프레드 연산자 (*) 사용
    val numberArray = intArrayOf(6, 7, 8, 9, 10)
    printNumbers(*numberArray)
}
```

- 중위함수 호출: 인자가가 하나뿐인 메서드를 간편하게 호출할 수있다.
```kotlin
// Int에 대한 확장 함수로 정의
infix fun Int.add(other: Int): Int {
    return this + other
}

fun main() {
    // 중위 함수 호출
    val result1 = 10 add 20  // 중위 호출
    val result2 = 10.add(20) // 일반 호출

    println("Result1: $result1") // 30
    println("Result2: $result2") // 30
}
```

- 구조 분해 선언 : 복합적인 값을 분해하여 여러변수에 나눠담는다.
```kotlin
data class Person(val name: String, val age: Int)

fun main() {
    val person = Person("Alice", 25)

    // 구조 분해 선언
    val (name, age) = person

    println("Name: $name") // Name: Alice
    println("Age: $age")   // Age: 25
}
```

### 3.4.1 자바 컬렉션 API 확장
- 코틀린은 자바컬렉션을 사용하면서도 더 많은 기능을 제공한다. 이는 확장함수를 이용헀기에 가능한 것이다.


### 3.4.2 가변 인자 함수: 인자의 개수가 달라질 수 있는 함수 정의
- vararg 키워드를 사용하여 가변 인자를 받을 수 있다.
- 배열앞에 * 를 붙이면 실제 값을 넘겨준다.
```kotlin
fun main() {
    val numbers = arrayOf(1, 2, 3)

    // 스프레드 연산자를 사용한 경우
    val listWithSpread = listOf("Numbers:", *numbers)

    // 스프레드 연산자를 사용하지 않은 경우
    val listWithoutSpread = listOf("Numbers:", numbers)

    println("With Spread: $listWithSpread")
    println("Without Spread: $listWithoutSpread")
}

// With Spread: [Numbers:, 1, 2, 3]
// Without Spread: [Numbers:, [Ljava.lang.Integer;@3b07d329]
```

### 3.4.3 값의 쌍 다루기: 중위 호출과 구조 분해 선언
```kotlin
// Int에 대한 확장 함수로 정의
infix fun Int.add(other: Int): Int {
    return this + other
}
```
- 위에서 예시를 들었다 실제로 map을 set 할때 1 to "one" 이런식으로 사용한다.

- 구조분해 선언
```kotlin
for ((index, element) in collection.withIndex()) {
  println("$index: $element")
}
```

### 3.5 문자열가 정규식 다루기
- 코틀린 예시
```kotlin
fun main() {
  val str = "a,b,,c"

  // 기본 split (정규식 사용)
  println(str.split(",")) // 결과: [a, b, , c]

  // 빈 문자열 제거 (limit 사용)
  println(str.split(",", limit = 2)) // 결과: [a, b,,c]

  // 특정 문자로 분리
  println(str.split('a')) // 결과: [, ,b,,c]
}

```

- 자바 예시
```java
public class Main {
  public static void main(String[] args) {
    String str = "a,b,,c";

    // 기본 split
    String[] result = str.split(",");
    System.out.println(Arrays.toString(result)); // 결과: [a, b, c]

    // 빈 문자열 포함시키려면 Pattern 사용
    String[] resultWithEmpty = str.split(",", -1);
    System.out.println(Arrays.toString(resultWithEmpty)); // 결과: [a, b, , c]
  }
}
```

### 주요 차이점

| **특징**                         | **Kotlin**                          | **Java**                            |
|-----------------------------------|--------------------------------------|--------------------------------------|
| **빈 문자열 포함 여부**           | 기본적으로 빈 문자열 포함           | 기본적으로 빈 문자열 제외           |
| **구분자 타입**                   | 문자, 문자열, 정규식 모두 지원       | 정규식만 지원                       |
| **빈 문자열 포함 강제**           | 기본적으로 포함                     | `split(delimiter, -1)` 사용 필요    |
| **limit 매개변수 사용 가능 여부** | 가능                                | 가능                                |

---


- 3중 따움표 문자열 예시
- 3중 따움표 문자열은 문자열에 포함된 모든 문자를 그대로 유지한다.
- 3중 따움표 문자열은 여러 줄로 이루어진 문자열을 표현할 때 유용하다.
- $를 사용하여 변수를 문자열에 포함시킬 수 있다. 만약 $를 문자열에 포함시키고 싶다면 '$'로 사용한다.
```kotlin
fun main() {
    val multiLineString = """
      This is a multiline string.
      You can write text over multiple lines
      without using \n for newlines.
    """

    println(multiLineString)
}
```


### 3.6 코드 다듬기 : 로컬 함수와 확장

```kotlin
class User(val id: Int, val name: String, val address: String)

fun saveUser(user: User) {

    fun validate(user: User,
                 value: String,
                 fieldName: String) {
        if (value.isEmpty()) {
            throw IllegalArgumentException(
                "Can't save user ${user.id}: empty $fieldName") 
        }
    }

    validate(user, user.name, "Name")
    validate(user, user.address, "Address")

}
```
- 불필요한 중복을 제거할떄 유용하게 사용된다.
- 로컬함수의 경우, 상위 메서드의 변수에 접근이 가능하다.

- 확장함수로 사용하게되면 클래스에는 포함시키고 싶지않지만, 다른곳에서는 사용되지않는 검증로직을 수신객체를 지정하지 않고도 공개된 맴버 프로퍼니나 메서드에 접근할 수 있다.
```kotlin
class User(val id: Int, val name: String, val address: String)

fun User.validateBeforeSave() {
  fun validate(value: String,
               fieldName: String) {
    if (value.isEmpty()) {
      throw IllegalArgumentException(
        "Can't save user ${user.id}: empty $fieldName")
    }
  }

  validate(name, "Name")
  validate(address, "Address")
}

fun saveUser(user: User) {
    user.validateBeforeSave()
}
```

