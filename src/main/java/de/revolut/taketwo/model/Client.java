package de.revolut.taketwo.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Client {
    private String email;

    public Client(String email) {
        this.email = email;
    }
}
