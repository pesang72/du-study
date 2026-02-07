# 옵셔널에서 널이 될 수 있는 타입으로

## 없음을 표현하기

### 코틀린은 널을 포용한다
- Java에서는 모든 참조 타입이 null이 될 수 있어서, NPE(NullPointerException)가 런타임에 터진다
- Java의 해결책인 `Optional`은 래퍼 객체를 만들어 null을 회피하는 방식이다
- Kotlin은 다른 접근을 택했다: null을 피하지 않고, **타입 시스템에 포함**시켰다

```kotlin
// null 불가 - 컴파일 에러
var name: String = null  // Error!

// null 허용 - ?를 붙여야 한다
var name: String? = null  // OK
```

- `String`과 `String?`은 다른 타입이다. 컴파일러가 null 가능성을 **컴파일 타임에 검사**한다
- null이 될 수 있는 타입은 안전 호출(`?.`), 엘비스 연산자(`?:`) 등을 통해 안전하게 다룬다

```kotlin
val length = name?.length          // name이 null이면 length도 null
val length = name?.length ?: 0     // name이 null이면 0
val length = name!!.length         // name이 null이면 NPE (Java처럼 위험)
```

- 즉, Java는 Optional로 null을 **감싸서 회피**하고, Kotlin은 `?` 타입으로 null을 **언어 차원에서 포용**한다


## 옵셔널에서 널 가능성으로 리펙터링 하기

- Java코드   
[JLegs.java](../src/main/java/com/example/ch4/JLegs.java)

- 변환된 코틀린  
- [Legs.kt](../src/main/kotlin/com/example/ch4/Legs.kt)

- 코드가 간결해질 수 있는 극단적인 예시로 null관련 로직과 takeIf 라는 확장함수로 인해 Optional관련 로직이 사라졌다