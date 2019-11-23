package de.revolut.taketwo.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BalanceTest {
    public static final BigDecimal HUNDRED_BUX = new BigDecimal("100.00");
    private static final BigDecimal NEGATIVE_AMOUNT = new BigDecimal("-100.00");
    private Balance balance;

    @BeforeEach
    void init() {
        balance = new Balance(BigDecimal.ZERO);
        assertThat(balance).isNotNull();
    }

    @Test
    void constructor_negativeBalance() {
        assertThrows(Balance.NegativeAmountException.class, () -> {
            new Balance(NEGATIVE_AMOUNT);
        });
    }

    @Test
    void add() {
        Balance newBalance = balance.addCash(HUNDRED_BUX);
        assertThat(newBalance).isEqualTo(new Balance(HUNDRED_BUX));
    }

    @Test
    void add_negative() {
        assertThrows(Balance.NegativeAmountException.class, () -> {
            balance.addCash(NEGATIVE_AMOUNT);
        });
    }

    @Test
    void subtract() {
        balance.addCash(HUNDRED_BUX);
        Balance newBalance = balance.subtract(HUNDRED_BUX);
        assertThat(newBalance).isEqualTo(new Balance(new BigDecimal("0.00")));
    }

    @Test
    void subtract_negative() {
        assertThrows(Balance.NegativeAmountException.class, () -> {
            balance.subtract(NEGATIVE_AMOUNT);
        });
    }
}
