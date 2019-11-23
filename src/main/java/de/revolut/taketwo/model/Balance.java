package de.revolut.taketwo.model;

import java.math.BigDecimal;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@EqualsAndHashCode
@Data
public class Balance {
    private BigDecimal amount;

    public Balance(@NonNull BigDecimal amount) {
        validateNegativeAmount(amount);
        this.amount = amount;
    }

    private void validateNegativeAmount(@NonNull BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeAmountException("Given amount is negative");
        }
    }

    public Balance addCash(BigDecimal incomingCash) {
        validateNegativeAmount(incomingCash);
        this.amount = this.amount.add(incomingCash);
        return this;
    }

    public Balance subtract(BigDecimal amount) {
        validateNegativeAmount(amount);
        if (this.amount.compareTo(amount) < 0) {
            throw new NegativeAmountException("Insufficient funds");
        }
        this.amount = this.amount.subtract(amount);
        return this;
    }

    public class NegativeAmountException extends IllegalArgumentException {
        NegativeAmountException(String message) {
            super(message);
        }
    }
}