package de.revolut.taketwo.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public final class Client {
    private final String email;

    public Client(String email) {
        this.email = email;
    }
}
