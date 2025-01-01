# 7 연산자 오버로딩과 기타 관레

### 7.1 산술 연산자 오버로딩
#### 7.1.1 이항 산술 연산 오버로딩
- 코틀린에서는 산술 연산자를 오버로딩할 수 있다.
- 반드시 operator 키워드를 사용해야 한다.
- 확장함수로도 정의가 가능하다.
```kotlin
data class Point(val x: Int, val y: Int) {
    operator fun plus(other: Point): Point {
        return Point(x + other.x, y + other.y)
    }
}

val p1 = Point(10, 20)
val p2 = Point(30, 40)
println(p1 + p2)

operator fun Point.minus(other: Point): Point {
    return Point(x - other.x, y - other.y)
}
```
### 산술 연산자와 대응하는 함수
| **연산자** | **함수 이름**   |
|------------|-----------------|
| `+`        | `plus`         |
| `-`        | `minus`        |
| `*`        | `times`        |
| `/`        | `div`          |
| `%`        | `rem`          |
| `..`       | `rangeTo`      |



#### 7.1.2 복합 대입 연산자 오버로딩
- `+=`, `-=`, `*=`, `/=`, `%=` 연산자를 오버로딩할 수 있다.

| **연산자** | **함수 이름**   |
|------------|-----------------|
| `+=`       | `plusAssign`   |
| `-=`       | `minusAssign`  |
| `*=`       | `timesAssign`  |
| `/=`       | `divAssign`    |
| `%=`       | `remAssign`    |

- 이론적으로는 += 를 plus, plusAssign 둘다 오버로딩 할 수 있지만, 실제로는 val를 통해 plusAssign를 사용못하게 하거나, plus처럼 새로운 값을 반환하는식으로 명확하게 하는게 좋다
- 컬렉션에 대해서, plus는 새로운 컬렉션을 반환하고, plusAssign은 기존 컬렉션을 변경한다.


#### 7.1.3 단한 연산자 오버로딩
- 단항 연산자 예시
```kotlin
operator fun Point.unaryMinus(): Point { // 단항 minus 함수는 파라미터가 없다.
    return Point(-x, -y) // 음수의 새로운 객체를 반환
}

val p = Point(10, 20)
println(-p)
```

##### 기타 관련 연산자
| **연산자** | **함수 이름**    |
|---------|--------------|
| `+a`    | `unaryPlus`  |
| `-a`    | `unaryMinus` |
| `!a`    | `not`        |
| `++`    | `inc`        |
| `--`    | `dec`        |


#### 7.2 비교 연산자 오버로딩
- 코틀린은 자바랑 다르게 == 나 비교연산자를 직접사용 할 수 있기에 좀더 직관적이다.

#### 7.2.1 동등성 연산자: equals
- 코틀린이 == 연산자 호출을 equals 호출로 변경하는걸 앞장에서 배운적이 있따.
- 동일하게 != 또한 equals를 호출한다.
- null이 아닌경우에 한하여 equals를 호출한다.
- equals 의 경우, operator가 붙지않는다. Any에 정의되어있는 메서드이기 때문이다. (Any에는 operator가 정의되어있다.)


#### 7.2.2 순서 비교 연산자: compareTo
- compareTo는 Comparable 인터페이스에 정의되어있다. 객체의 크기르르 비교하여 정수로 나타내어 준다.
- 비교연산자는 내부에 정의한 compareTo를 호출한다. ( < 는 compareTo < 0, ==는 compareTo == 0, >= 는 compareTo > 0)
```kotlin
 a >= b // a.compareTo(b) >= 0

class Person(val firstName: String, val lastName: String) : Comparable<Person> {
    override fun compareTo(other: Person): Int {
        return compareValuesBy(this, other, Person::lastName, Person::firstName)
    }
}

val p1 = Person("Alice", "Smith")
val p2 = Person("Bob", "Johnson")
print(p1 < p2) // false
```
- compareValuesBy는 여러개의 프로퍼티를 비교할 수 있게 해준다. (순서대로 비교한다.)

### 7.3 컬렉션과 범위에 대해 쓸 수 있는 관례

#### 7.3.1 인덱스로 원소에 접근: get과 set
- 코틀린은 인덱스 연산자도 관례를 따른다. Map과 MutablemMap 내부를 확인해보자
- get과 set을 오버로딩하면 [] 연산자를 사용할 수 있다.
```kotlin
operator fun Point.get(index: Int): Int {
    return when(index) {
        0 -> x
        1 -> y
        else -> throw IndexOutOfBoundsException("Invalid coordinate $index")
    }
}

val p = Point(10, 20)
println(p[1]) // 20

data class MutablePoint(var x: Int, var y: Int)
operator fun MutablePoint.set(index: Int, value: Int) {
    when(index) {
        0 -> x = value
        1 -> y = value
        else ->
            throw IndexOutOfBoundsException("Invalid coordinate $index")
    }
}

val p = MutablePoint(10, 20)
p[1] = 42
println(p) // x = 10, y = 42
```


#### 7.3.2 in 관례
- in 연산자는 contains를 호출한다.
- in의 우항에 있는 객체는 contains 메서드의 수신 객체가 되고, in의 좌항에 있는 객체는 인자가 된다.
- a in b -> b.contains(a)
- a !in b -> !b.contains(a)
```kotlin
data class Rectangle(val upperLeft: Point, val lowerRight: Point)

operator fun Rectangle.contains(p: Point) : Boolean {
    return p.x in upperLeft.x.until lowerRight.x &&
            p.y in upperLeft.y.until lowerRight.y
}

val rect = Rectangle(Point(10, 20), Point(50, 50))
println(Point(20, 30) in rect) // true
println(Point(5, 5) in rect) // false
```

#### 7.4.3 rangeTo 관례
- rangeTo는 .. 연산자를 호출한다.
- a..b -> a.rangeTo(b)
- rangeTo는 Comparable 인터페이스를 구현한 객체에 대해 호출할 수 있다.
- rangeTo는 Comparable 인터페이스를 구현한 객체를 반환한다.
```kotlin
val now = LocalDate.now()
val vacation = now..now.plusDays(10)
println(now.plusWeeks(1) in vacation) // true
```

#### 7.3.4 for 루프를 위한 iterator 관례
- for루프는 in 연산자를 사용하지만 내부의 list.iterator()를 호출하여 이터레이터를 얻어 사용한다. next()와 hasNext()를 제공한다.
- 하지만 이 또한 관례이므로, 문자열에 대한 for루프도 iterator를 제공하는 예시가 있다.
```kotlin
operator furn CharSequence.iterator(): CharIterator
for(c in "abc") {
    println(c)
}

// 날짜 범위에 대한 이터레이터 구현하기
operator fun ClosedRange<LocalDate>.iterator(): Iterator<LocalDate> =
    object : Iterator<LocalDate> {
        var current = start
        override fun hasNext() = current <= endInclusive
        override fun next() = current.apply {
            current = plusDays(1)
        }
    }

// 예시
val newYear = LocalDate.ofYearDay(2020, 1)
val daysOff = newYear.minusDays(1)..newYear.plusDays(1)
for(dayOff in daysOff) {
    println(dayOff)
}
```


### 7.4 구조 분해 선언과 component 함수
- 구조 분해 선언은 객체의 여러 프로퍼티를 개별 변수로 분해할 수 있다.
- data 클래스는 컴파일러가 componentN 함수를 자동으로 생성한다.
- listOf등에서 코틀린 표준 라이브러리에서 기본적으로 5개의 componentN 함수를 제공한다.
- 초과되면 이런 에러가 발생한다. Destructuring declaration initializer of type List<Int> must have a 'component6()' function

```kotlin
class CustomPoint(val x: Int, val y: Int, val z: Int) {
    operator fun component1() = x
    operator fun component2() = y
    operator fun component3() = z
    operator fun component4() = x + y + z
}

fun main() {
    val (x, y, z, sum) = CustomPoint(1, 2, 3)
    println("$x, $y, $z, $sum") // 출력: 1, 2, 3, 6
}
```

#### 7.4.1 구조 분해 선언과 루프
- 구조 분해 선언은 루프에서도 사용할 수 있다.
- 루프에서는 index와 value를 분해할 수 있다.
```kotlin
val map = mapOf(1 to "one", 2 to "two")
for((key, value) in map) {
    println("$key -> $value")
}
```

### 7.5 프로프터 접근자 로직 재활용: 위임 프로퍼티
#### 7.5.1 위임 프로퍼티
- 위임 프로퍼티란? 프로퍼티의 get, set을 다른 객체에게 위임하는 것이다. 위임 키워드는 by 키워드를 통해 선언

```kotlin
class Delegate {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return "Value for '${property.name}'"
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        println("Setting '${property.name}' to '$value'")
    }
}

class Example {
    var name: String by Delegate()
}

fun main() {
    val example = Example()
    println(example.name)  // getValue 호출 -> "Value for 'name'"
    example.name = "Kotlin" // setValue 호출 -> "Setting 'name' to 'Kotlin'"
}

```

#### 7.5.2 위임 프로퍼티 사용: by lazy()를 사용한 프로퍼티 초기화 지연
- 지연 초기화는 객체의 일부분을 초기화하지 않고 남겨뒀다가 실제로 그 값이 필요할때, 초기화에 쓰이는 패턴이다
- by lazy는 위임 프로퍼티를 사용하여 구현된다.
```kotlin
class Example {
    val lazyValue: String by lazy {
        println("Computed!")
        "Hello, Lazy!"
    }
}

fun main() {
    val example = Example()
    println(example.lazyValue) // "Computed!" 출력, 그리고 "Hello, Lazy!" 반환
    println(example.lazyValue) // 캐시된 값을 반환: "Hello, Lazy!"
}
```

#### 7.5.3 위임 프로퍼티 구현
- 위의 Delegate 예시로 대채한다.
- by를 통해 위임이 가능하나, 우항에 있는 식을 계산할 결과인 객체는 getValue, setValue를 구현해야한다.

#### 7.5.4 위임 프로퍼티 컴파일 규칙
- 컴파일러는 모든 프로퍼티 접근자 안에 getValue, setValue 호출코드를 생성해준다.

컴파일 전 코드
```kotlin
class Example {
    var property: String by Delegate()
}
```

컴파일 된 코드
```kotlin
class Example {
    private val delegate = Delegate()
    var property: String 

    fun getProperty(): String {
        return delegate.getValue(this, ::property)
    }

    fun setProperty(value: String) {
        delegate.setValue(this, ::property, value)
    }
}
```

#### 7.5.5 프로퍼티 값을 맵에 저장
- Map을 활용하여 위임 프로퍼티를 활용할 수 있다.
```kotlin
class Config(val map: MutableMap<String, Any?>) {
    var theme: String by map
    var fontSize: Int by map
    var notificationsEnabled: Boolean by map
}

fun main() {
    // 초기 설정값을 가진 MutableMap 생성
    val settings = mutableMapOf(
        "theme" to "dark",
        "fontSize" to 14,
        "notificationsEnabled" to true
    )

    // Config 클래스 인스턴스 생성
    val config = Config(settings)

    // 초기값 출력
    println("Theme: ${config.theme}")                  // Theme: dark
    println("Font Size: ${config.fontSize}")          // Font Size: 14
    println("Notifications: ${config.notificationsEnabled}") // Notifications: true

    // 값 변경
    config.theme = "light"
    config.fontSize = 16
    config.notificationsEnabled = false

    // 변경된 값 출력
    println("Updated Theme: ${config.theme}")                  // Updated Theme: light
    println("Updated Font Size: ${config.fontSize}")           // Updated Font Size: 16
    println("Updated Notifications: ${config.notificationsEnabled}") // Updated Notifications: false

    // MutableMap의 내용 확인
    println("Underlying Map: $settings")
    // 출력: Underlying Map: {theme=light, fontSize=16, notificationsEnabled=false}
}
```

#### 7.5.6 프레임워크에서 위임 프로퍼티 활용
- 만약 User라는 객체가 있다고 가정할 때 Setter의 활동에 따라 다음과같이 로그를 찍는 예시가 있을수 있다.
```kotlin
import kotlin.properties.Delegates

class User {
    var name: String by Delegates.observable("Unknown") { property, oldValue, newValue ->
        println("${property.name} changed from $oldValue to $newValue")
    }
}

fun main() {
    val user = User()
    user.name = "Alice"  // 출력: name changed from Unknown to Alice
    user.name = "Bob"    // 출력: name changed from Alice to Bob
}
```
