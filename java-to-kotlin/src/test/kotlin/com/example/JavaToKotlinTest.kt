package com.example

import com.example.ch3.EmailAddress
import com.example.ch3.EmailAddress.Companion.parse
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class EmailAddressTests {
    @Test
    fun parsing() {
        Assertions.assertEquals(
            EmailAddress("fred", "example.com"),
            parse("fred@example.com")
        )
    }

    @Test
    fun parsingFailures() {
        Assertions.assertThrows(
            IllegalArgumentException::class.java
        ) { parse("@") }
        Assertions.assertThrows(
            IllegalArgumentException::class.java
        ) { parse("fred@") }
        Assertions.assertThrows(
            IllegalArgumentException::class.java
        ) { parse("@example.com") }
        Assertions.assertThrows(
            IllegalArgumentException::class.java
        ) { parse("fred_at_example.com") }
    }

    @Test
    fun parsingWithAtInLocalPart() {
        Assertions.assertEquals(
            EmailAddress("\"b@t\"", "example.com"),
            parse("\"b@t\"@example.com")
        )
    }
}