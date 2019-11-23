package de.revolut.taketwo.db;

import de.revolut.taketwo.model.Balance;
import de.revolut.taketwo.model.Client;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class BankDao implements Bank {
    private static final BankDao INSTANCE = new BankDao();
    private final ConcurrentMap<Client, Balance> bankRevolut;

    private BankDao() {
        bankRevolut = new ConcurrentHashMap<>();
    }

    public static BankDao getInstance() {
        return INSTANCE;
    }

    @Override
    public void createClient(Client client) {
        if (bankRevolut.putIfAbsent(client, new Balance(BigDecimal.ZERO)) != null) {
            throw new ClientAlreadyExist();
        }
    }

    @Override
    public Balance balance(Client client) {
        if (!bankRevolut.containsKey(client)) {
            throw new ClientNotFound();
        }
        return bankRevolut.get(client);
    }

    @Override
    synchronized public Balance deposit(Client client, BigDecimal amount) {
        Balance newBalance = bankRevolut.computeIfPresent(client,
                                                          (ignore, balance) -> balance.addCash(
                                                              amount));
        if (newBalance == null) {
            throw new ClientNotFound();
        }
        return newBalance;
    }

    @Override
    synchronized public void withdraw(Client client, BigDecimal amount) {
        if (bankRevolut.computeIfPresent(client, (client1, balance) -> balance.subtract(amount))
            == null) {
            throw new ClientNotFound();
        }
    }

    @Override
    synchronized public void transfer(Client sender, Client receiver, BigDecimal amount) {
        balance(receiver);
        withdraw(sender, amount);
        deposit(receiver, amount);
    }

    public class ClientAlreadyExist extends IllegalArgumentException {}

    public class ClientNotFound extends IllegalArgumentException {}


}
