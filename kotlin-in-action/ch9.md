# 9 제네릭스

### 9.1 제네릭 타입 라마티머
- 제네릭 타입을 사용하면, 타입을 인자로 받는 클래스나 함수를 만들 수 있다.
- 제네릭 타입의 인스턴스를 만드려면 타입 파라미터를 구체적인 타입인자로 치환해아한다. 
- 예를 들어 Map<V, K> 타입의 인스턴스를 만들려면 V와 K를 구체적인 타입으로 치환해야한다. Map<String, Integer>


* 코틀린 컴파일러는 타입인자를 추론할 수 있다. (자바도 동일하다.)
```kotlin
val authors = listOf("Dmitry", "Svetlana") // List<String>을 추론
val readers = mutableListOf<String>() // 빈 리스트를 생성할때, 타입을 명시
```


#### 9.1.1 제네릭 함수와 프로퍼티
- 제네릭 함수를 호출할때는 반드시 구체적인 타입으로 타입 인자를 넘겨야 한다. 직접 명시하거나, 컴파일러가 타입을 추론할 수 있어야한다.
```kotlin
fun <T> List<T>.slice(indices: IntRange): List<T> {
    return this.subList(indices.start, indices.endInclusive + 1)
}

val letters = ('a'..'z').toList()
println(letters.slice<Char>(0..2)) // [a, b, c]

// 타입 추론
println(letters.slice(10..13)) // [k, l, m, n]
```

- 위에서 이미 봤지만, 제네릭 확장 프로퍼티를 선언할 수 있다.
```kotlin
val <T> List<T>.penultimate: T
    get() = this[size - 2]

val letters = ('a'..'z').toList() // 여기서는 Char로 추론
println(letters.penultimate) // y
```

##### 확장 프로퍼티만 제네렉하게 선언할 수 있다.
- 클레스 프로퍼티에 여러타입의 값을 저장할 수는 없으니 제네릭한 일반 프로퍼티는 말이 되지않는다.
- 일반 프로퍼티는 클래스의 인스턴스와 함께 메모리에 저장되므로, 고정된 타입을 가져야만 한다. 여러 타입을 허용하는 제네릭한 프로퍼티는 일반적으로 말이 되지 않으며, 이를 방지하기 위해 확장 프로퍼티와 같은 구조가 필요하다.


#### 9.1.2 제네릭 클래스 선언
- 자바와 동일하게 < > 기호를 통하여 클래스 또는 인터페이스를 제네릭하게 만들수 있다.
```kotlin
interface Repository<T> {
    fun save(item: T)
    fun findById(id: Int): T
}

class Box<T>(var value: T)
```

제네릭 클래스를 확장하는 클래스를 정의하려면 기반 타입의 제네릭 파라미터에 대해 타입 인자를 지정해야 한다.

- 구체적인 타입을 넘길 수 있다. (앞에서 많은 예시가 있었다.)
- 타입 파라미터로 받은 타입을 넘길 수도 있다.
```kotlin
class Box<T>(val value: T)

class Wrapper<T>(val box: Box<T>) {
    fun getBoxValue(): T {
        return box.value
    }
}

fun main() {
    val intBox = Box(42) // Box에 Int 타입 전달
    val wrapper = Wrapper(intBox) // Wrapper에 Int 타입 전달

    println(wrapper.getBoxValue()) // 출력: 42
}
```


#### 9.1.3 타입 파라미터 제약
- Kotlin에서 타입 파라미터 제약은 제네릭 타입에 조건을 추가하여 특정 타입이나 기능을 요구하는 것을 말한다.
- 아무런 상한을 지정하지 않은 타입 파라미터는 Any?를 상한으로 가진다.

1. 상위 클래스 또는 인터페이스 제약
```kotlin
fun <T : Number> sum(a: T, b: T): T {
    // T는 Number의 하위 클래스여야 함
    return a.toDouble() + b.toDouble() as T
}
```

2. 여러 제약 조건
```kotlin
fun <T> copyIfGreater(list: List<T>, threshold: T) where T : Comparable<T>, T : Cloneable {
    // T는 Comparable<T>와 Cloneable 인터페이스를 구현해야 함
}

```

#### 9.1.4 타입 파라미터를 널이 될 수 없는 타입으로 한정
- 상한을 지정하지않았을때 타입 파라미터는 Any?를 상한으로 정한 파라미터와 같아진다.
- 타입 파라미터를 널이 될 수 없는 타입으로 한정하려면 Any 대신 Any를 상한으로 지정한다.
```kotlin
fun <T> acceptNullable(value: T) {
    println(value)
}

acceptNullable(42) // Int
acceptNullable(null) // Nothing?


fun <T : Any> acceptNotNull(value: T) {
    println(value)
}

acceptNotNull(42) // Int
acceptNotNull(null) // 컴파일 에러
```


### 9.2 실행 시 제네릭스의 동작 : 소거된 타입 파라미터와 실체화된 타입 파라미터
- 제네릭 클래스의 인스턴스를 만들 때, 타입 인자 정보는 컴파일러에 의해 지워진다. 이를 타입 소거라고 한다.

<div style="border: 1px solid #1a1a1a; padding: 10px; border-radius: 5px; background-color: #171c17;">
<strong>함수를 <code>inline</code>으로 만들면 타입 인자가 지워지지 않게 할 수 있는데, 이를 실체화라고 한다.</strong>
</div>

#### 9.2.1 실행 시점의 제네릭: 타입 검사와 캐스트
- 자바와 마찬가지로, 제네릭 타입 인자 정보는 런타임에 지워진다. 따라서 런타임에는 제네릭 타입 인자에 대한 정보를 알 수 없다.
- List<String> -> List로 추론이된다.
- 런타임에서 해당 타입을 알아내기 위해 is 연산자를 사용할 수 있다. 그리고 스타프로젝션 문법을 사용할 수 있다.
- as와 as?는 실행 시점의 타입 정보를 기반으로 작동한다. 

```kotlin
fun printSum(c: Collection<*>) {
    val intList = c as? List<Int> ?: throw IllegalArgumentException("List is expected")
    println(intList.sum())
}

printSum(listOf(1, 2, 3)) // 6
printSum(listOf("a", "b", "c")) // IllegalArgumentException: List is expected
```
- c as? List<Int> :  실행 시점에서는 제네릭 타입 정보(Int)가 소거되기 때문에 c가 List인지 여부만 확인
- sum() : 실행 시점에 모든 요소가 Int라고 가정합니다. 만약 내부 요소가 Int가 아니면, 런타임 예외가 발생
    


#### 9.2.2 실체화한 타입 파라미터를 사용한 함수 선언
- inline 함수와 reified 키워드를 사용하면 **타입 파라미터를 실행 시점에 실체화(reified)**하여 사용할 수 있다.
- inline의 경우, 함수를 호출하는 곳에 함수의 본문(바이트코드)을 복사해 넣는다. 실체화된 타입 파라미터를 통해 타입 정보를 실행 시점에 유지하며, 타입 관련 작업을 수행할 수 있다.


```kotlin
// 타입검사
inline fun <reified T> isInstance(value: Any): Boolean {
    return value is T
}

fun main() {
    println(isInstance<String>("Hello")) // true
    println(isInstance<Int>("Hello"))   // false
}

// 타입캐스팅
inline fun <reified T> cast(value: Any): T? {
    return value as? T
}

fun main() {
    val result = cast<String>("Hello")
    println(result) // "Hello"

    val failed = cast<Int>("Hello")
    println(failed) // null
}

// 클래스 정보 접근
inline fun <reified T> printClassName() {
    println(T::class.java)
    println(T::class)

}

fun main() {
    printClassName<String>() // class kotlin.String
    printClassName<Int>()    // class kotlin.Int
}


```
- 타입체크 is : 실체화된 타입 파라미터를 사용하면 타입을 is로 검사할 수 있다.
- 타입 캐스트 as : 실체화된 타입 파라미터를 사용하면 타입을 as로 캐스트할 수 있다.
- 클래스 정보 접근 (T::class) : 실체화된 타입 파라미터를 사용하면 클래스 정보에 접근할 수 있다. (뒷장에서 더 나올예정)


#### 9.2.4 실체화한 파라미터의 제약
- 다음과 같은 경우에는 실체화한 타입 파라미터를 사용할 수 있다.
  - 타입 검사와 캐스팅 (is, !is, as, as?)
  - 리플렉션
  - 코틀린 타입에 대응하는 'java,lang.Class' 를 얻기
  - 다른 함수를 호출할 때 타입 인자로 사용
- 다음과 같은 경우에는 실체화한 타입 파라미터를 사용할 수 없다.
  - 타입 파라미터 클래스의 인스턴스 생성
  - 타입 파라미터 클래스의 동반 객체 메서드 호출
  - inline 함수가 아닌 타입 파라미터를 reified로 지정
  - 실체화한 타입 파라미터를 요구하는 함수를 호출하면서 실체화하지 않은 타입 파라미터로 받은 타입을 타입 인자로 넘기기

> inline 함수가 아닌 타입 파라미터를 reified로 지정 <br />
> 실체화한 타입 파라미터를 인라인 함수에만 사용할 수 있으므로 실체화한 타입 파라미터를 사용하는 함수는 자신에게 전달되는 모든 람다와 함께 인라이닝된다.
> 람다 내부에서 타입 파라미터를 사용하는 방식에 따라서는 람다를 인라이닝할 수 없는 경우가 생기기도 하고 개발자들이 성능 문제로 람다를 인라이닝하고 싶지 않을 수도 있다.
> 이런 경우 8장에서 살펴본 noinline 변경자를 함수 타입 파라미터에 붙여서 인라이닝을 금지할 수 있다.


### 9.3 변성 : 제네릭과 하위 타입
- 기저 타입(List)이 같고 타입 인자가 다른 여러 타입이 서로 어떤 관계에 있는지를 설명하는 개념

#### 9.3.1 변성이 있는경우 : 인자를 함수에 넘기기
- List<Any> 타입의 파라미터를 받는 함수에 List<String>을 넘기는 것은 안전한가?
  - String은 Any를 확장하므로 Any타입에 String을 넘겨도 안전하다.
  - 하지만 List 인터페이스의 타입인자로 들어가는 경우, 안전하지 않다.

```kotlin
fun addToList(list: MutableList<Any>) {
    list.add(42) // Int 타입 추가
}

fun main() {
    val stringList: MutableList<String> = mutableListOf("Hello", "World")
    // addToList(stringList) // 컴파일 에러: MutableList<String>은 MutableList<Any>와 호환되지 않음
}
```
- String은 Any를 확장하지만, List<String>은 List<Any>가 아니다
- 코틀린은 이런 문제를 방지하기 위해 제네릭 타입에 대해 무공변을 기본으로 적용한다. (뒤에 좀 더 자세히)

#### 9.3.2 클래스, 타입, 하위 타입
- var x : String? 처럼 같은 크랠스 이름을 널이 될 수 있는 타입에도 쓸 수 있다. 이는 둘 이상의 타입을 구성할 수 있다는 얘기다

> 하위 타입 : 어떤 타입 A의 값이 필요한 모든 장소에 타입 B의 값을 사용할 수 있을 때, 타입 B는 타입 A의 하위 타입이다. ex Number는 Int의 상위 타입이다
- 널이 될 수 없는 타입은 널이 될 수 있는 타입의 하위 타입이다. 하지만 두 타입 모두 같은 클래스에 해당하는 경우가있다.
- 무공변 (Invariance) : 무공변은 제네릭 타입이 상속 관계를 따르지 않는 것을 의미 즉, A가 B를 상속하더라도, List< A> 는 List< B>를 상속하지 않는다.
  - 무공변은 타입 안정성을 위해 필요하다.
```kotlin
fun addToAnyList(list: MutableList<Any>) {
    list.add(42) // Int 추가
}

val stringList: MutableList<String> = mutableListOf("Hello", "World")
// addToAnyList(stringList) // 만약 허용된다면?

println(stringList) // "Hello", "World", 42? - 타입 오류!
```

#### 9.3.3 공변성 : 하위 타입 관계를 유지
- 공변성(Covariance) : 제네릭 타입이 하위 타입 관계를 유지하도록 선언하는 것을 의미
- 공변성의 경우 위에서 설명한 A가 B의 하위 타입이라면, List< A>도 List< B>의 하위 타입이 성립한다.
- out 키워드를 사용하여 공변성을 선언한다.
- 공변성의 조건
  - 타입 파라미터를 출력 위치에서만 사용해야 하며, 입력으로는 사용할 수 없다.

1. 읽기 전용 (공변성)
```kotlin
fun printAll(items: List<Any>) {
    items.forEach { println(it) }
}

fun main() {
    val strings: List<String> = listOf("Kotlin", "Java")
    printAll(strings) // 허용: List<String>은 List<Any>의 하위 타입
}
```
- List는 기본적으로 공변적(out T)으로 선언되어 있다.

2. Mutable 리스트 (무공변성)
```kotlin
fun addItem(items: MutableList<Any>) {
    items.add(42) // Any 타입 추가
}

fun main() {
    val strings: MutableList<String> = mutableListOf("Kotlin", "Java")
    // addItem(strings) // 컴파일 에러: MutableList<String>은 MutableList<Any>와 호환되지 않음
}
```
- MutableList는 무공변적으로 선언되어 있어, 타입 안전성을 유지

- out 키워드의의미
  - 공변성 : 하위 타입 관계가 유지된다.
  - 사용제한 : T를 아웃 위치에서만 사용 가능하다.
```kotlin
interface Producer<out T> {
    fun produce(): T // 출력 위치에서만 사용 가능
    // fun consume(item: T) // 컴파일 에러: 입력 위치에서 사용 불가
}
```


#### 9.3.4 반공변성 : 뒤집힌 하위 타입 관계
- **반공변성(Contravariance)**은 제네릭 타입의 하위 타입 관계를 뒤집는 것
-  A가 B의 하위 타입이라면, Consumer< B>는 Consumer< A>의 하위 타입이 된다.
- in 키워드를 통하여 선언
- 반공변성의 조건
  - 입력 전용 제네릭 타입에서 사용
  - 입력 위치에서만 사용 가능하다.

반공변성의 예제
```kotlin
open class Animal
class Dog : Animal()

fun feedAnimals(animals: MutableList<in Dog>) {
    animals.add(Dog()) // Dog 추가 가능
}

fun main() {
    val animals: MutableList<Animal> = mutableListOf()
    feedAnimals(animals) // Animal 리스트에 Dog를 추가 가능
}
```
- MutableList<in Dog>는 MutableList<Animal>로 동작할 수 있다.
- Dog를 Animal의 하위 타입으로 간주하여 입력 작업을 허용

반공변성 인터페이스
```kotlin
interface Consumer<in T> {
    fun consume(item: T)
}

fun useConsumer(consumer: Consumer<Animal>) {
    consumer.consume(Animal())
}

fun main() {
    val dogConsumer: Consumer<Dog> = object : Consumer<Dog> {
        override fun consume(item: Dog) {
            println("Consuming a dog")
        }
    }

    useConsumer(dogConsumer) // 허용: Consumer<Dog>는 Consumer<Animal>의 하위 타입
}
```

| 특성             | 공변성 (`out`)                 | 반공변성 (`in`)                 | 무공변성 (기본)             |
|------------------|--------------------------------|---------------------------------|----------------------------|
| 관계 유지 방향    | 하위 타입 관계 유지             | 하위 타입 관계를 뒤집음          | 하위 타입 관계 없음         |
| 사용 위치        | 출력 위치                      | 입력 위치                      | 입력 및 출력 모두 가능     |
| 키워드           | `out`                         | `in`                           | 없음                       |
| 타입 안전성      | 읽기 전용으로 타입 안전성 보장  | 쓰기 전용으로 타입 안전성 보장   | 타입 안전성을 직접 관리    |
| 예제             | `List<out T>` (읽기 전용)     | `MutableList<in T>` (쓰기 전용) | `MutableList<T>`          |


- Function1 같은경우, 공변적이면서 반공변적이다.
```kotlin
interface Function1<in P, out R> {
  operator fun invoke(p: P): R
}
```


#### 9.3.5 사용 지점 변성 : 타입이 언급되는 지점에서 변성 지정
- 자바에서는 타입 파라미터가 있는 타입을 사용할 때마다 해당 타입 파라미터를 하위 타입이나 상위 타입 중 어떤 타입으로 대치할 수 있는지 명시해야 한다. 이런 방식을 사용 지점 변성이라 부른다.
- 선언부가 아닌 사용되는 곳에서 변성을 지정하는 방식
- kotlin 에서는 in, out을 이용하여 지정

```kotlin
fun <T> copyData(source: MutableList<out T>, // 공변성 적용
                 destination: MutableList<T>) {
    for (item in source) {
        destination.add(item)
    } 
}

val source: MutableList<Int> = mutableListOf(1, 2, 3)
val destination: MutableList<Number> = mutableListOf()

copyData(source, destination)
println(destination) // 출력: [1, 2, 3]
```
- MutableList<out T> : source는 읽기 전용으로 사용되며, 데이터를 추가하거나 변경할 수는 없다.
- 이로 인해 MutableList< Int> 가 MutableList< out Number>로 전달이 된다.
- source는 공변성으로 선언되었으므로 읽기만 가능하며, 데이터를 안전하게 처리할 수 있다.
- destination은 쓰기 작업이 가능하므로, source의 데이터를 안전하게 추가할 수 있다.

- 반공변의 예제
```kotlin
fun <T> fillData(destination: MutableList<in T>, value: T) {
    destination.add(value)
}

fun main() {
    val numbers: MutableList<Number> = mutableListOf(1, 2, 3)
    val integers: MutableList<Int> = mutableListOf()

    // 사용 예 1: Number 리스트에 Int 추가
    fillData(numbers, 42) // MutableList<Number>에 Int 추가 가능
    println(numbers) // 출력: [1, 2, 3, 42]

    // 사용 예 2: Int 리스트에 Int 추가
    fillData(integers, 7) // MutableList<Int>에 Int 추가 가능
    println(integers) // 출력: [7]
}
```
- numbers는 MutableList< Number> 이며, in T를 사용했으므로, T 타입의 하위 타입(Int) 도 허용된다.


#### 9.3.6 스타 프로젝션: 타입 인자 대신 * 사용
- **스타 프로젝션(*)**은 타입 파라미터를 알 수 없거나 명확히 지정할 필요가 없을 때 사용한다.
- 스타프로젝션의 경우 out Any? 처럼 동작한다. 즉 꺼내올 수는 있지만, 마음대로 넣을수는 없다.

```kotlin
fun printList(list: List<*>) {
    for (item in list) {
        println(item) // 모든 요소는 Any?로 간주
    }
}

fun main() {
    val strings: List<String> = listOf("Kotlin", "Java")
    printList(strings) // 동작
}

// 쓰기제한
fun addItem(list: MutableList<*>) {
    // list.add("New Item") // 컴파일 에러: 쓰기는 허용되지 않음
}
```


> Consumer<in T>와 같은 반공변 타입 파라미터에 대한 스타 프로젝션은 <in Nothing>과 동등하다
- 스타 프로젝션(Consumer<*>)은 타입 파라미터를 명시하지 않는 상황에서 타입 안전성을 유지하기 위한 방법
- 반공변 타입 파라미터에서 스타 프로젝션을 사용하면, **타입 파라미터가 아무 값도 허용하지 않는 가장 좁은 타입인 Nothing**으로 대체
- Nothing : Kotlin의 타입으로 어떠한 값도 허옹하지 않는 타입 즉 Consumer<in Nothing>은 값을 소비할 수 없는 Consumer를 의미한다.
- Consumer<*>를 사용하는 경우 

1. 제네릭 타입의 Consumer를 전달받아 타입 검사 : 타입 소거로 인해 consume 메서드를 호출하여 값을 전달하는 것은 불가능하지만, 타입에 관계없이 객체를 처리할 수 있다.
```kotlin
interface Consumer<in T> {
  fun consume(item: T)
}

fun processConsumers(consumers: List<Consumer<*>>) {
  for (consumer in consumers) {
    println("Processing a consumer of type: ${consumer::class}")
  }
}

fun main() {
  val stringConsumer = object : Consumer<String> {
    override fun consume(item: String) {
      println("Consumed a String: $item")
    }
  }

  val intConsumer = object : Consumer<Int> {
    override fun consume(item: Int) {
      println("Consumed an Int: $item")
    }
  }

  val consumers = listOf<Consumer<*>>(stringConsumer, intConsumer)

  processConsumers(consumers)
  // 출력:
  // Processing a consumer of type: class ...
  // Processing a consumer of type: class ...
}

```
