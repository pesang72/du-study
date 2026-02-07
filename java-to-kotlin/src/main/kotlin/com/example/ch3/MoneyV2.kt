package com.example.ch3

import java.math.BigDecimal
import java.util.*

data class MoneyV2
private constructor(
    val amount: BigDecimal,
    val currency: Currency
) {
    fun add(that: MoneyV2): MoneyV2 {
        require(currency == that.currency) {
            "cannot add Money values of different currencies"
        }
        return MoneyV2(amount.add(that.amount), currency)
    }

    companion object {
        @JvmStatic
        fun of(amount: BigDecimal, currency: Currency): MoneyV2 {
            return MoneyV2(
                amount.setScale(currency.defaultFractionDigits),
                currency
            )
        }

        @JvmStatic
        fun of(amountStr: String?, currency: Currency): MoneyV2 {
            return of(BigDecimal(amountStr), currency)
        }

        @JvmStatic
        fun of(amount: Int, currency: Currency): MoneyV2 {
            return of(BigDecimal(amount), currency)
        }

        @JvmStatic
        fun zero(userCurrency: Currency): MoneyV2 {
            return of(BigDecimal.ZERO, userCurrency)
        }
    }
}