package com.nabgha.catalogue.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MoneyTest {

    @Test
    void should_create_money_when_amount_is_positive() {
        Money money = Money.of(100.50, "EUR");

        assertThat(money.amount()).isEqualByComparingTo(new BigDecimal("100.50"));
        assertThat(money.currency().getCurrencyCode()).isEqualTo("EUR");
    }

    @Test
    void should_throw_exception_when_amount_is_negative() {
        assertThatThrownBy(() -> Money.of(-10.0, "EUR"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be negative");
    }

    @Test
    void should_add_money_with_same_currency() {
        Money m1 = Money.of(10.0, "USD");
        Money m2 = Money.of(25.5, "USD");

        Money result = m1.add(m2);

        assertThat(result.amount()).isEqualByComparingTo(new BigDecimal("35.50"));
    }

    @Test
    void should_throw_exception_when_adding_different_currencies() {
        Money m1 = Money.of(10.0, "USD");
        Money m2 = Money.of(25.5, "EUR");

        assertThatThrownBy(() -> m1.add(m2))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
