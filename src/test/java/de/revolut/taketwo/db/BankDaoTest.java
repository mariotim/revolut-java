package de.revolut.taketwo.db;

import de.revolut.taketwo.model.Balance;
import de.revolut.taketwo.model.Client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BankDaoTest {
    private static final BigDecimal HUNDRED_BUX = new BigDecimal("100.00");
    private Bank bank;

    @BeforeEach
    void init() {
        bank = BankDao.getInstance();
        assertThat(bank).isNotNull();
    }

    @Test
    void createClient() {
        bank.createClient(new Client("email1"));
    }

    @Test
    void createClient_AlreadyExist() {
        final Client client = new Client("email2");
        bank.createClient(client);
        assertThrows(BankDao.ClientAlreadyExist.class, () -> bank.createClient(client));
    }

    @Test
    void balance() {
        final Client client = new Client("email3");
        bank.createClient(client);
        Balance balance = bank.balance(client);
        assertThat(balance).isEqualTo(new Balance(BigDecimal.ZERO));
    }

    @Test
    void balance_ClientNotFound() {
        assertThrows(BankDao.ClientNotFound.class,
                     () -> bank.balance(new Client("non_existing_client@email.com")));
    }

    @Test
    void deposit() {
        final Client client = new Client("email4");
        bank.createClient(client);
        Balance balance = bank.deposit(client, HUNDRED_BUX);
        assertThat(balance).isEqualTo(new Balance(HUNDRED_BUX));
    }

    @Test
    void deposit_NoClientFound() {
        assertThrows(BankDao.ClientNotFound.class,
                     () -> bank.deposit(new Client("non_existing_client@email.com"), HUNDRED_BUX));
    }

    @Test
    void deposit_negativeAmount() {
        final Client client = new Client("email5");
        bank.createClient(client);
        assertThrows(Balance.NegativeAmountException.class,
                     () -> bank.deposit(client, new BigDecimal("-100.00")));
    }

    @Test
    void withdraw_insufficientFunds() {
        final Client client = new Client("email6");
        bank.createClient(client);
        assertThrows(Balance.NegativeAmountException.class,
                     () -> bank.withdraw(client, HUNDRED_BUX));

    }

    @Test
    void withdraw() {
        final Client client = new Client("email7");
        bank.createClient(client);
        bank.deposit(client, HUNDRED_BUX);
        bank.withdraw(client, HUNDRED_BUX);
        final Balance expectedBalance = new Balance(new BigDecimal("0.00"));
        assertThat(bank.balance(client)).isEqualTo(expectedBalance);
    }

    @Test
    void withdraw_clientNotFound() {
        final Client clientDoesntExist = new Client("bloodibloop");
        assertThrows(BankDao.ClientNotFound.class,
                     () -> bank.withdraw(clientDoesntExist, HUNDRED_BUX));

    }

    @Test
    void transfer() {
        final Client sender = new Client("email8");
        final Client receiver = new Client("email9");
        bank.createClient(sender);
        bank.createClient(receiver);
        bank.deposit(sender, HUNDRED_BUX);
        bank.transfer(sender, receiver, HUNDRED_BUX);
    }
}
