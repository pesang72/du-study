# 자바 클래스에서 코틀린으로

## 간단한 값 타입
간단한 자바코드 변경해보기   
[EmailAddress.java](../src/main/java/com/example/ch3/OldEmailAddress.java)

변환된 코틀린  
[EmailAddress.kt](../src/main/kotlin/com/example/ch3/EmailAddress.kt)

1. companion object
- 간단히 말하면, Java의 static 메서드/필드를 Kotlin에서 표현하는 방식
- companion object는 사실 클래스 안에 선언된 싱글톤 객체
- 이름을 추가할 수 있다 (기본 이름은 Companion)
- companion object 안에 선언된 멤버들은 클래스 이름으로 바로 접근 가능
  - EmailAddress.parse("
- 인터페이스를 구현하거나 다른 클래스를 상속 가능 (Java static으로는 불가능, 이렇게 쓸 필요가 있는지는 확인필요)



2. @JvmStatic 
- @JvmStatic을 빼면 Kotlin에서 호출할 때는 차이가 없지만, Java에서 호출할 때 달라집니다.

- @JvmStatic 있을 때 (Java에서)
  - EmailAddress email = EmailAddress.parse("user@example.com");

- @JvmStatic 없을 때 (Java에서)
  - EmailAddress email = EmailAddress.Companion.parse("user@example.com");
 
ompanion을 거쳐야 해서 기존 Java 코드가 깨집니다. 즉, @JvmStatic은 Java에서 기존처럼 EmailAddress.parse()로 바로 호출할 수 있도록 Java 호환성을 유지해주는
어노테이션입니다.


3. getter/setter 가 명시적으로 들어있다 (val, var)  
[Marketing.java](../src/main/java/com/example/ch3/Marketing.java)


4. data class를 사용하는경우, equals(), hashCode(), toString() 메서드가 자동으로 생성된다.
- copy() 메서드 자동 생성: 일부 프로퍼티만 변경한 복사본을 만들 수 있다
  - val updated = email.copy(domain = "new.com")
- 구조 분해 선언(destructuring) 지원: componentN() 함수가 자동 생성된다
  - val (localPart, domain) = email
- 주 생성자에 최소 하나의 val 또는 var 파라미터가 필요하다
  - data class는 주 생성자의 val/var 파라미터를 기반으로 equals(), hashCode(), toString(), copy(), componentN()을 자동 생성한다
  - 파라미터가 없으면 자동 생성할 기준이 없기 때문에 컴파일 에러가 발생한다
```kotlin
// 컴파일 에러 - 파라미터가 없음
data class Empty()

// OK - 최소 하나 있으면 됨
data class Email(val address: String)
```
  - 주의: 주 생성자가 아닌 body에 선언된 프로퍼티는 자동 생성 대상에서 제외된다
```kotlin
data class EmailAddress(val localPart: String, val domain: String) {
    var tag: String = ""  // body에 선언된 프로퍼티
}

val a = EmailAddress("user", "example.com").apply { tag = "work" }
val b = EmailAddress("user", "example.com").apply { tag = "personal" }

a == b  // true! tag는 equals 비교에 포함되지 않음
```
[EmailAddressV2.kt](../src/main/kotlin/com/example/ch3/EmailAddress.kt)


## 데이터 클래스의 한계
- 데이터 클래스의 단점은 캡슐화를 제공하지 않는다는 점이 있다.
- 예전버전의 경우 private final 필드로 선언되어 외부에서 직접 접근할 수 없었지만, 데이터 클래스에서는 프로퍼티가 public으로 노출된다.

java 버전  
[OldMoney.java](../src/main/java/com/example/ch3/OldMoney.java)

kotlin 버전  
[Money.kt](../src/main/kotlin/com/example/ch3/Money.kt)  
[MoneyV2.kt](../src/main/kotlin/com/example/ch3/MoneyV2.kt)


```kotlin
    val money = MoneyV2.of("100.50".toBigDecimal(), java.util.Currency.getInstance("USD"))
    val money2 = money.copy(amount = "50.25".toBigDecimal())
```

- copy()를 실제로 막기 시작한 건 Kotlin 2.0.20 부터는 막힌상태

