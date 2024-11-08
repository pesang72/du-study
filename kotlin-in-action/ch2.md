# 2장 코틀린 기초

### 2.1.1  hello world
```kotlin
fun main(args: Array<String>) {
    println("Hello, world!")
}
```
- 함수를 선언할 때, fun 키워드를 사용한다.
- 파라미터 이름 뒤에 타라미터의 타입을 사용한다.
- 꼭 클레스안에 넣을 필요가 없다.
- 줄 끝에 ; 을 붙이지 않아도 된다.


<br /><br />
### 2.1.2 함수
```kotlin
fun max(a: Int, b: Int): Int {
    return if (a > b) a else b
}

fun max(a: Int, b: Int): Int = if (a > b) a else b
```
- if문은 문(statement)이 아닌 식(expression)이다.
  - 식은 값을 만들어 내며, 다른 식의 하위 요소로 계산에 참여 가능
- 리턴 값의 경우 : 뒤에 타입을 적어준다.
- 식이 본문인 함수 : 중괄호, return을 생략하고 함수를 작성할 수 있다.
- 타입 추론 : 함수의 반환 타입을 생략할 수 있다 블록 타입은 생략 불가능
```kotlin
fun add(a: Int, b: Int) = a + b // 생략 가능

fun add(a: Int, b: Int): Int { // 생략 불가능
    return a + b
}
```

<br /><br />
### 2.1.3 변수
```kotlin
val question = "삶, 우주, 그리고 모든 것에 대한 궁극적인 질문"
val answer = 42
val answer: Int = 42
```
- 변수 타입을 생략할 수 있다. (명시하는게 편할것 같다)
- 초기화 식이 없는경우에는 반드시 타입을 명시해야한다.


#### 변경 가능한 변수와 변경 불가능한 변수
- val(value에서 따옴) : 변경 불가능한 참조를 저장하는 변수 (자바의 final 변수와 비슷), 초기화 후에는 재대입이 불가능하다.
- var(variable에서 따옴) : 변경 가능한 참조를 저장하는 변수, 일반 변수에 사용
- 기본적으로 val을 사용하되, 변경이 필요한 경우에만 var를 사용한다. (setter를 줄이는 의미인것같다)

```kotlin
val message: String
if (canPerformOperation()) {
    message = "Success"
} else {
    message = "Failed"
}

val languages = arrayListOf("Java") // 불변참조를 선언
languages.add("Kotlin") // add는 내부 Collection을 변경하는것으로 가능
```

- var 의 경우, 변수의 값 변경이 가능하지만, 변수의 타입은 고정되어 바뀌지 않는다
```kotlin
var answer = 42
answer = "no answer" // ❌ Error: type mismatch 컴파일 오류 발생
```

<br /><br />
### 2.1.4 문자열 템플릿
```kotlin
val name = if (args.size > 0) args[0] else "Kotlin"
println("Hello, $name!")

// 2개 이상의 변수나 식을 문자열에 넣고싶다면
println("\$x") // -> $x출력

// 문자열을 넣고싶다면 {중괄호 사용}
println("Hello, ${args[0]}!")
```

- 문자열 템플릿 : 문자열 중간에 변수나 식을 넣을 수 있다.
- 한글을 변수명으로 사용하는경우, 한글이 이어 붙는경우 unresolved reference 오류가 발생한다. (한글 변수명은 지양하는게 좋을것 같다) (ex '$name님' -> '${name}님')
- 컴파일된 코든느 StringBuilder를 통해 문자열 상수와 변수의 값을 append로 연결한다. (바이트코드를 생성해준다.)
- 중괄호로 둘러쌓인 식에서 " 사용가능 (ex. println("Hello, ${if (args.size > 0) args[0] else "someone"}!"))


### 2.2 클래스와 프로퍼티
```kotlin
// java클랙스
public class Person {
    private final String name;

    public Person(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

// 코틀린 클래스
class Person(val name: String)
```
- 코틀린의 기본 가시성은 public이다 (생략가능)
- 맴버 필드의 가시성은 private이다 (생략가능)
- 클래스의 기본 상속은 final이다 (상속을 허용하려면 open 키워드를 사용해야한다)
- 일반적으로 getter를 제공한다.

### 2.2.1 프로퍼티
- 자바에서는 필드와 접근자 를 묶어 property라고 부른다.
```kotlin
class Person(val name: String, var study: Boolean)

val person = Person("Bob", true)
println(person.name)
println(person.study)
person.study = false
```
- val : 읽기 전용 프로퍼티 (getter만 생성)
- var : 변경 가능한 프로퍼티 (getter, setter 생성)

### 2.2.2 커스텀 접근자
- 커스텀 접근자를 사용하면 프로퍼티의 값을 계산할 수 있다.
```kotlin
class Rectangle(val height: Int, val width: Int) {
    val isSquare: Boolean
        get() {
            return height == width
        }
}

val rectangle = Rectangle(41, 43)
println(rectangle.isSquare)

```


### 2.2.3 코틀린 소스코드 구조 : 디렉터리와 패키지
- 디렉터리 구조와 패키지 구조는 일치하지 않아도 된다.
  - 자바의 경우 디랙터리 구조가 패키지 구조를 그대로 따랴야한다. 
    - ex. src/com/example/MyClass.java -> package com.example;
    - ex. src/com/example/MyClass.kt -> package com.example
  - 하지만.. 이렇게하는 경우는 본적도없다. 보통 디렉터리 구조와 패키지 구조를 일치시킨다.

### 2.3.1 enum 클래스 정의
- enum 클래스는 자바와 비슷하다.
```kotlin
// 프로퍼티 없는 enum 클래스
enum class Color {
    RED, ORANGE, YELLOW, GREEN, BLUE, INDIGO, VIOLET
}

    
// 프로퍼티 있는 enum 클래스
enum class Color(val r :Int, val g: Int, val b: Int)  {
  RED(255, 0, 0), ORANGE(255, 165, 0), YELLOW(255, 255, 0),
  ;
  
  fun rgb() = (r * 256 + g) * 256 + b
}
```

### 2.3.2 when 구문 으로 enum 클래스 다루기
- java의 switch에 해당하는 코틀린 구성 요소는 when
- when또한 값을 만들어내는 식 이다.
```kotlin
fun getXX(color: Color) = 
  when(color) {
    Color.RED -> "red"
    Color.ORANGE -> "orange"
    Color.YELLOW -> "yellow"
  }

// 한 when 분기 안에 여러값
fun getWarmth(color: Color) = 
  when(color) {
    Color.RED, Color.ORANGE, Color.YELLOW -> "warm"
    Color.GREEN -> "neutral"
    Color.BLUE, Color.INDIGO, Color.VIOLET -> "cold"
  }
```

<br>

- when의 분기 조건에 상수 대신 함수를 사용할 수 있다.
  - 객체 사이를 매치할 때 동등성을 사용
  - when 식은 인자 값과 매치하는 조건 값을 찾을 때까지 각 분기를 검사

```kotlin
fun mix(c1: Color, c2: Color) =
    when (setOf(c1, c2)) { //  인자는 비교 가능한 객체라면 아무거나 가능
        setOf(RED, YELLOW) -> ORANGE
        setOf(BLUE, VIOLET) -> INDIGO
        setOf(YELLOW, BLUE) -> GREEN
        else -> throw Exception("dirty color")
    }    
```

- 인자가 없는 when
  - when의 인자로 아무것도 넘기지 않으면 각 분기의 조건은 불리언 식이어야 한다.
```kotlin
...
when {
    c1 == Color.RED && c2.color == Color.YELLOW -> Color.ORANGE
    else -> throw Exception("dirty color")
}
```

### 2.3.5 스마트 캐스트
```kotlin
interface Expr // Expr 인터페이스 선언
class Num(val value: Int) : Expr
class Sum(val left: Expr, val right: Expr) : Expr

println(eval(Sum(Sum(Num(1), Num(2)), Num(4))) // 7
```
- Expr 인터페이스를 상속받는 Num, Sum 클래스를 선언
- 위 println을 하는 과정에서, 식이 수 라면 값을 반환, 식이 합계라면 좌항과 우항의 값을 계산한다면 더한값을 반환
- 코틀린에서는 java의 instanceof를 사용하지 않고, is를 사용한다. + 컴파일러가 캐스팅까지 수행해준다. (스마트 캐스트)
- 명시적으로 캐스팅을 하려면 as를 사용한다. (ex. e as Num)
```kotlin
fun eval(e: Expr): Int =
    if (e is Num) {
        e.value
    } else if (e is Sum) {
        eval(e.left) + eval(e.right)
    } else {
        throw IllegalArgumentException("Unknown expression")
    }

println(eval(Sum(Sum(Num(1), Num(2)), Num(4))) // 7
```
- if문 when 모두 block {} 을 사용할 수 있다. 이 경우 블럭의 마지막 문장이 블럭 전체의 결과가 된다.


<br /><br />

### 2.4 while과 for 루프
- while은 차이가 없다. (do-while도 동일)
- for문은 자바와 다르다. ( for (i in 1..100) { ... } )
  - in 연산자는 범위를 만들어낸다. (1..100) -> 1부터 100까지의 범위를 만들어낸다.
  - in 연산자는 범위를 검사한다. (if (i in 1..100) { ... } )
  - in 연산자는 컬렉션을 순회한다. (for (i in list) { ... } )
  - in 연산자는 맵을 순회한다. (for ((key, value) in map) { ... } )
  - in 연산자는 문자열을 순회한다. (for (c in "abc") { ... } )
  - in 연산자는 사용자 정의 클래스를 순회한다. (for (e in elements) { ... } )
  - 추후에 downTo, until, step도 기록할 예정
```kotlin

// 1부터 100까지 진행되는 range
for (i in 1..100) {
    println(i)
}

// 100부터 4까지 ( 100, 98, 96, ... , 4)
// downTo 값 까지 진행
for (i in 100 downTo 4 step 2) {
    println(i)
}

// 인덱스와 함께 출력
val list = arrayListOf("10", "11", "1001")
for ((index, element) in list.withIndex()) {
    println("$index: $element")
}
// 0: 10, 1: 11, 2: 1001

// map
val map = TreeMap<Char, String>()
for (c in 'A'..'F') {
    val binary = Integer.toBinaryString(c.toInt())
    map[c] = binary
}
for ((letter, binary) in map) {
    println("$letter = $binary")
}
// A = 1000001, B = 1000010, C = 1000011, D = 1000100, E = 1000101, F = 1000110
```

### 2.4.4 in으로 컬렉션과 범위 검사

- in 연산자는 컬렉션에 원소가 포함되어 있는지 검사한다.
- in 연산자는 범위에 값이 포함되어 있는지 검사한다.
- in 연산자는 when과 함께 사용할 수 있다.
```kotlin
fun isLetter(c: Char) = c in 'a'..'z' || c in 'A'..'Z'
fun isNotDigit(c: Char) = c !in '0'..'9'

println(isLetter('q')) // true
println(isNotDigit('X')) // true

// when
fun recognize(c: Char) = when(c) {
    in '0'..'9' -> "It's a digit!"
    in 'a'..'z', in 'A'..'Z' -> "It's a letter"
    else -> "I don't know.."
}
```

2.5 코틀린의 예외처리
- 클래스의 차이로 new없이 throw를 한다.
- throws절이 코드에 없다. 이유는 체크예외를 구분하지 않는다.
- try,catch 또한 식이다. try의 갑을 변수로 대입하거나 리턴이 된다. 단 반드시 중괄호 {}로 둘러싸야한다.
```kotlin
fun readNumber(reader: BufferedReader): Int? {
    return try {
        val line = reader.readLine()
        Integer.parseInt(line)
    } catch (e: NumberFormatException) {
        null
    } finally {
        reader.close()
    }
}
```