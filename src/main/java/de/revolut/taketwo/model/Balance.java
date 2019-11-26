package de.revolut.taketwo.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@EqualsAndHashCode
@Data
public final class Balance {
    private final BigDecimal amount;

    private Balance() {
        this(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
    }

    public Balance(@NonNull BigDecimal amount) {
        validateNegativeAmount(amount);
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    private void validateNegativeAmount(@NonNull BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeAmountException("Given amount is negative");
        }
    }

    public Balance addCash(BigDecimal incomingCash) {
        validateNegativeAmount(incomingCash);
        return new Balance(this.amount.add(incomingCash));
    }

    public Balance subtract(BigDecimal amount) {
        validateNegativeAmount(amount);
        if (this.amount.compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }
        return new Balance(this.amount.subtract(amount));
    }

    public class NegativeAmountException extends IllegalArgumentException {
        NegativeAmountException(String message) {
            super(message);
        }
    }

    public class InsufficientFundsException extends IllegalStateException {
        InsufficientFundsException(String message) {
            super(message);
        }
    }
}
