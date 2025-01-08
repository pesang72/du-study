# 8 고차 함수 : 파라미터와 반환 값으로 람다사용

### 8.1 고차함수 정의
- 고차함수는 간단하게 정의하면 함수를 인자로 받고나 함수를 반환하는 함수다
- 우리가 흔하게 쓰는 java의 stream 중간연산자들이 고차함수의 예시다

#### 8.8.1 함수 탕비
- 햠수를 매개변수나 반환값으로 다룰 때 그 형태를 지정한다.
- 함수 타입을 선언할 때에는 Unit을 명시해야한다.
```kotlin
// 구체적인 타입을 지정하는경우
val sum: (Int, Int) -> Int = { a, b -> a + b }
val action: () -> Unit = { println(42) }

// 컴파일러가 타입을 추론하는 경우
val sum = { x: Int, y: Int -> x + y }
val action = { println(42) }
```

- 함수타입에서도 반환타입을 널이 될 수 있는 타입으로 지정할 수 있다.
- 고차함수 자체가 널이 될 수도 있다.
```kotlin
fun findEvenOrNull(numbers: List<Int>): (Int) -> String? {
    return { number -> if (number % 2 == 0) "Even: $number" else null }
}

fun main() {
    val numbers = listOf(1, 2, 3, 4, 5)
    val evenChecker = findEvenOrNull(numbers)

    numbers.forEach { number ->
        val result = evenChecker(number)
        println("Number $number -> $result")
    }
}

// 고차함수 자체가 널이 될 가능성이 있는경우
fun findEvenOrNull(numbers: List<Int>): ((Int) -> String)? {
    return { number -> if (number % 2 == 0) "Even: $number" else "No" }
}

fun main() {
    val numbers = listOf(1, 2, 3, 4, 5)
    val evenChecker = findEvenOrNull(numbers)

    numbers.forEach { number ->
        val result = evenChecker?.invoke(number)
        println("Number $number -> $result")
    }
}
```

#### 8.1.2 인자로 받은 함수 호출
- 함수 타입의 변수를 선언하고, 그 변수를 호출할 수 있다.
```kotlin
fun twoAndThree(operation: (Int, Int) -> Int) {
    val result = operation(2, 3)
    println("The result is $result")
}

twoAndThree({ a, b -> a + b })
twoAndThree { a, b -> a * b }

```

fun String.filter(predicate : (Char) -> Boolean) : String
- predicate : 파라미터의 이름
- (Char) -> Boolean : 파라미터 함수 타입 타입
- 메서드의  반환 타입 : String
```kotlin
fun String.filter(predicate : (Char) -> Boolean) : String {
    val sb = StringBuilder()
    for (index in 0 until length) {
        val element = get(index)
        if (predicate(element)) sb.append(element)
    }
    return sb.toString()
}

println("ablc".filter { it in 'a'..'z' }) // ablc
```


#### 8.1.3 자바에서 코틀린 함수 타입 사용
- 코틀린에서 정의된 함수 타입이 함수형 인터페이스로 컴파일된다.
- FuntionN 인터페이스를 구현한 클래스를 사용한다.
- Consumer<String> : (String) -> Unit
- Function<T, R> : (T) -> R
- Predicate<T>: (T) -> Boolean
- Supplier<R>: () -> R
```kotlin
// Kotlin code
fun performOperation(x: Int, y: Int, operation: (Int, Int) -> Int): Int {
    return operation(x, y)
}
```
```java
import kotlin.jvm.functions.Function2;

public class KotlinFunctionTypeExample {
    public static void main(String[] args) {
        // 코틀린 함수 타입에 해당하는 함수형 인터페이스 구현
        Function2<Integer, Integer, Integer> addOperation = (x, y) -> x + y;
        Function2<Integer, Integer, Integer> multiplyOperation = (x, y) -> x * y;

        // 코틀린 함수 호출
        int result1 = KotlinInteropKt.performOperation(5, 10, addOperation);
        int result2 = KotlinInteropKt.performOperation(5, 10, multiplyOperation);

        System.out.println("Addition Result: " + result1); // 15
        System.out.println("Multiplication Result: " + result2); // 50
    }
}
```


#### 8.1.4 디폴트 값을 지정한 함수 타입 파라미터나 널이 될 수 있는 함수 타입 파라미터
- 디폴트 값으로 함수타입을 지정해둘 수 있다. 
- 아래 예시를 보면 operation: (Int) -> Int = { it * 2 } 가 디폴트가 된다.
```kotlin
fun printResult(x: Int, operation: (Int) -> Int = { it * 2 }) {
    println(operation(x))
}

fun main() {
    printResult(3) // 디폴트 값 사용: 출력 6
    printResult(3) { it + 1 } // 람다 전달: 출력 4
}
```

- 함수 타입 파라미터를 nullable로 선언할 수 있다.
- 이 경우 호출 전에 null 검사를 수행하거나 엘비스 연산자를 사용해야한다.
```kotlin
fun processValue(x: Int, operation: ((Int) -> Int)? = null) {
    val result = operation?.invoke(x) ?: "No operation"
    println(result)
}

fun main() {
    processValue(3) // null 처리: 출력 "No operation"
    processValue(3) { it * 3 } // 람다 전달: 출력 9
}
```

#### 8.1.5 함수에서 함수를 반환
- 위에 이미 예시를 둔 케이스가 있다.
- 함수안에서 함수를 반환하여 실행을 할 수 있다.
```kotlin
fun operation(): (Int) -> Int {
    return { x -> x * 2 }
}

fun main() {
    val func = operation()
    println(func(2)) // 4
}
```

#### 8.1.6 람다를 이용한 중복 제거
- 자주 사용되지만, 코드가 중복되는 경우 람다를 사용하여 중복을 제거할 수 있다.
- 아래의 예시로 설명을 한다면, resource 에서 뭔가를 하는 작업이 있다고 가정할 때, try, finally 모아두고 close 작업을 공통화하여 중복을 제거할 수 있다.
```kotlin
fun <T> useResource(resource: Resource, action: (Resource) -> T): T {
    try {
        return action(resource)
    } finally {
        resource.close()
    }
}

fun main() {
    useResource(Resource()) { res ->
        res.doSomething()
    }
}
```


### 8.2 인라인 함수 : 람다의 부가 비용 없애기
- 컴파일러에게 함수 호출을 인라인으로 처리하도록 지시할 수 있다.
- 함수를 호출하는 대신, 함수의 본문 코드가 호출 지점에 직접 삽입된다.

#### 8.2.1 인라이닝이 작동하는 방식
- 인라인으로 메서드를 선언하는 경우, 메서드에 전달된 람다또한 인라이닝이 된다.
- 인라인 함수를 호출하면서 람다를 넘기는 경우에는 람다 코드를 알 수 없기에, 인라이닝 되지않는다.
```kotlin
inline fun inlineMethod(action: () -> Unit) {
    println("Before action")
    action() // 람다 코드도 호출 지점으로 인라인됨
    println("After action")
}

fun main() {
    inlineMethod {
        println("This is a lambda") // 람다가 인라인으로 처리됨
    }
}

// 컴파일 후 예상 동작 
fun main() {
    println("Before action")
    println("This is a lambda") // 람다 코드가 호출 지점에 복사됨
    println("After action")
}
```

- 람다를 인라이닝하지 않는 경우 <br />
-- action 람다가 함수로 전달되고 별도의 람다 객체로 처리  <br />
-- 호출 지점에서 람다가 인라인되지 않으므로, 람다 호출 오버헤드가 발생  <br />

```kotlin
inline fun inlineMethod(action: () -> Unit) {
    println("Before action")
    action() // 람다가 인라인되지 않음
    println("After action")
}

fun execute(action: () -> Unit) {
    inlineMethod(action) // 람다가 호출 지점에서 인라인되지 않음
}

fun main() {
    execute {
        println("This is a lambda") // 람다 코드가 인라인되지 않음
    }
}


// 컴파일 후 예상 동작
inline fun inlineMethod(action: () -> Unit) {
    println("Before action")
    action() // 람다가 인라인되지 않음
    println("After action")
}

fun execute(action: () -> Unit) {
    inlineMethod(action) // 람다가 호출 지점에서 인라인되지 않음
}

fun main() {
    execute {
        println("This is a lambda") // 람다 코드가 인라인되지 않음
    }
}
```

#### 8.2.2 인라인 함수의 한계
- 람다 캡처의 한계 : 람다가 외부 변수를 캡처하면, 인라인 함수가 복사되더라도 람다의 캡처 비용은 여전히 발생한다.
```kotlin
fun example() {
    val value = 42
    inlineFunction { println(value) } // value를 캡처하는 비용 발생
}
```

- 이외의 다른케이스
- 가상 메서드 :  가상 메서드는 런타임에 동적으로 호출 대상을 결정하기 때문에 컴파일 타임에 인라인 처리할 수 없다.
```kotlin
open class Base {
    open inline fun cannotInline() {} // 컴파일 에러
}
```

- 재귀함수 : 무한하게 복사될 위험이 있어 인라인 처리할 수 없다.
```kotlin
inline fun recursiveFunction(n: Int): Int {
    return if (n == 0) 0 else recursiveFunction(n - 1) // 컴파일 에러
}
```

#### 8.2.3 컬렉션 연산 인라이닝
- 일반적인 컬렉션 함수(map, filter 등)는 람다를 사용하는 고차 함수로 구현되어 있어, 람다 객체 생성과 함수 호출로 인해 런타임 성능 오버헤드가 발생한다.
- 코틀린 표준 라이브러리에서 컬렉션 연산은 대부분 **inline**으로 구현되어, 람다 호출 오버헤드와 중간 컬렉션 생성을 줄인다.

```kotlin
val result = listOf(1, 2, 3, 4)
    .filter { it % 2 == 0 }
    .map { it * 2 }

// 컴파일 후 예상 동작
val result = mutableListOf<Int>()
for (element in listOf(1, 2, 3, 4)) {
    if (element % 2 == 0) {
        result.add(element * 2)
    }
}
```
- 위의 예시를 보면, filter, map 함수가 인라인으로 처리되어, 람다 호출 오버헤드와 중간 컬렉션 생성을 줄인다.
- 중간 연산을 피하는 결과로 인하여 간단한 상황에서는 Sequence없이도 고성능 처리가 가능하다.

#### 8.2.4 함수를 인라인으로 선언해야 하는 이유
- 일반 함수 호출의 경우 이미 JVM은 최적화를 하고 있기 때문에, 인라인을 지원하고있다. JIT과정에서 인라인이 일어난다.
- 인라인 함수를 람다 인자와 함께 사용하는 케이스는 몇가지의 장점이 있다.
* 람다 호출 오버헤드제거 : 람다의 본문이 호출 지점으로 복사되므로 오버헤드 제거
* 중간 객체 생성방지 : 람다가 외부 변수를 참조하는 경우, 람다 객체가 생성되지만 인라인 함수를 사용하면 람다 객체 생성을 방지
* return문 사용 : 인라인 함수에서 람다를 사용할 경우, 람다 내에서 return을 호출해 외부 함수의 실행을 종료할 수 있습니다.

```kotlin
inline fun inlineAction(action: () -> Unit) {
    println("Start")
    action()
    println("End")
}

fun main() {
    inlineAction {
        println("Executing action")
        return // 외부 함수 종료
    }
    println("This line will not execute") // 실행되지 않음
}
```
- 큰 함수나 람다 사용 시, 호출 지점마다 코드가 복사되므로 코드가 길어질 수 있다.

#### 8.2.5 자원 관리를 위해 인라인된 람다 사용
- 어떤 자원을 획득하고, 작업을 하고, 자원을 해제하는 작업의 경우 람다로 중복을 없앨 수 있다. 이 경우, 자원해제등이 누락되는 휴먼에러를 막을 수 있다.
- 자바의 경우 try-with-resource 같은 기능이다. 코틀린의 use를 통하여 예시를 들어본다.
```kotlin
fun readFirstLineFromFile(path: String): String {
    BufferedReader(FileReader(path)).use { br ->
        return br.readLine()
    }
}
```

### 고차 함수 안에서 흐름 제어
- return에 대해서 몇가지 예시를 살펴본다.

#### 8.3.1 람다 안의 return문: 람다를 둘러싼 함수로부터 반환
- 람다 안에서 return을 사용하면, 람다를 둘러싼 함수로부터 반환된다.
- 자신을 둘러싸고 있는 블록보다 더 바깥에 있는 다른 블록을 반환하게 만드는 return을 넌로컬(non-local) return이라 한다.
- 람다를 둘러싼 함수로부터 반환하려면, 람다를 인라인으로 선언해야한다.
```kotlin
fun lookForAlice(people: List<Person>) {
    people.forEach {
        if (it.name == "Alice") return // 람다를 둘러싼 함수인 lookForAlice로부터 반환
    }
    println("Alice is not found")
}
```


#### 8.3.2 람다로부터 반환: 레이블을 사용한 return
- 람다식에서도 label을 사용하여 로컬 return을 할 수 있다.
- 인라인 함수의 이름을 return 뒤에 사용해도 된다.
```kotlin
fun lookForAlice(people: List<Person>) {
    people.forEach label@{
        if (it.name == "Alice") return@label // 람다를 둘러싼 함수인 lookForAlice로부터 반환
    }
    println("Alice might be somewhere") // 출력이 된다.
}


fun lookForAlice(people: List<Person>) {
    people.forEach {
        if (it.name == "Alice") return@forEach // 람다를 둘러싼 함수인 lookForAlice로부터 반환
    }
    println("Alice might be somewhere") // 출력이 된다.
}

```


#### 8.3.3 무명함수 : 기본적으로 로컬 return
- 코틀린에서 무명 함수(anonymous function)는 이름 없는 함수로, fun 키워드를 사용해 정의한다.
- **람다와 달리, 무명 함수는 기본적으로 로컬 return**을 수행ㅌㅈ

```kotlin
fun processNumbers(numbers: List<Int>) {
    numbers.forEach(fun(number): Unit {
        if (number == 3) return // 무명 함수 종료, 다음 요소로 이동
        println("Processing $number")
    })
    println("Finished processing")
}ㅌㅉ

fun main() {
    processNumbers(listOf(1, 2, 3, 4, 5))
}

/**
Processing 1
Processing 2
Processing 4
Processing 5
Finished processing
*/
```


