package com.example.ch3;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

import static java.math.BigDecimal.ZERO;

public class OldMoney {
    private final BigDecimal amount;
    private final Currency currency;

    private OldMoney(BigDecimal amount, Currency currency) { // <1>
        this.amount = amount;
        this.currency = currency;
    }

    public static OldMoney of(BigDecimal amount, Currency currency) { // <1>
        return new OldMoney(
            amount.setScale(currency.getDefaultFractionDigits()),
            currency);
    }


    public static OldMoney of(String amountStr, Currency currency) { // <2>
        return OldMoney.of(new BigDecimal(amountStr), currency);
    }

    public static OldMoney of(int amount, Currency currency) {
        return OldMoney.of(new BigDecimal(amount), currency);
    }

    public static OldMoney zero(Currency userCurrency) {
        return OldMoney.of(ZERO, userCurrency);
    }


    public BigDecimal getAmount() { // <2>
        return amount;
    }

    public Currency getCurrency() { // <3>
        return currency;
    }

    @Override
    public boolean equals(Object o) { // <3>
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OldMoney oldMoney = (OldMoney) o;
        return amount.equals(oldMoney.amount) &&
            currency.equals(oldMoney.currency);
    }

    @Override
    public int hashCode() { // <3>
        return Objects.hash(amount, currency);
    }

    @Override
    public String toString() { // <4>
        return amount.toString() + " " + currency.getCurrencyCode();
    }

    public OldMoney add(OldMoney that) { // <5>
        if (!this.currency.equals(that.currency)) {
            throw new IllegalArgumentException(
                "cannot add Money values of different currencies");
        }

        return new OldMoney(this.amount.add(that.amount), this.currency);
    }
}