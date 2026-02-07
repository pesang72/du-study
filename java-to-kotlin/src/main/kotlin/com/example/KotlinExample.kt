package com.example

import com.example.ch3.MoneyV2

data class KotlinExample(
    var name: String,
    var age: Int
) {
    fun greet(): String {
        return "Hello, I'm $name and I'm $age years old."
    }
}

fun main() {
    val person = KotlinExample("Alice", 30)

    val money = MoneyV2.of("100.50".toBigDecimal(), java.util.Currency.getInstance("USD"))
    val money2 = money.copy(amount = "50.25".toBigDecimal())

    println(person.greet())
}
