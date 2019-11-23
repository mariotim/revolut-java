package de.revolut.taketwo.db;

import de.revolut.taketwo.model.Balance;
import de.revolut.taketwo.model.Client;

import java.math.BigDecimal;

interface Bank {
    void createClient(Client client);

    Balance balance(Client client);

    Balance deposit(Client client, BigDecimal balance);

    void withdraw(Client client, BigDecimal balance);

    void transfer(Client sender, Client receiver, BigDecimal amount);
}
