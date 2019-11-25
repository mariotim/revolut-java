package de.revolut.taketwo;

import de.revolut.taketwo.model.Balance;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.math.BigDecimal;

import io.undertow.util.StatusCodes;
import jdk.incubator.http.HttpClient;
import jdk.incubator.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IntegrationTest {
    private static final BigDecimal HUNDRED_BUX = new BigDecimal("100.00");

    private static EntryPoint entryPoint;
    private static HttpRequestHelper httpRequestHelper;

    @BeforeAll
    static void init() {
        entryPoint = new EntryPoint();
        entryPoint.startServer();
        httpRequestHelper = new HttpRequestHelper(HttpClient.newHttpClient());
    }

    @AfterAll
    static void tearDown() {
        entryPoint.stopServer();
    }


    @Test
    void createClient() throws IOException, InterruptedException {
        final String email1 = "email1";
        httpRequestHelper.verifyCreateClient(email1);
    }


    @Test
    void createClient_Exist() throws IOException, InterruptedException {
        httpRequestHelper.verifyCreateClient("email2");
        HttpResponse<String> duplicateMail = httpRequestHelper.createClientResponse("email2");
        assertThat(duplicateMail.statusCode()).isEqualTo(StatusCodes.CONFLICT);
    }

    @Test
    void balance() throws IOException, InterruptedException {
        final String email = "email3";
        httpRequestHelper.verifyCreateClient(email);
        final Balance expectedBalance = new Balance(BigDecimal.ZERO);
        httpRequestHelper.verifyBalance(email, expectedBalance);
    }

    @Test
    void balance_noClientFound() throws IOException, InterruptedException {
        final String email = "email4";
        HttpResponse<String> response = httpRequestHelper.getBalanceResponse(email);
        assertThat(response.statusCode()).isEqualTo(StatusCodes.BAD_REQUEST);
    }

    @Test
    void deposit() throws IOException, InterruptedException {
        final String email = "email5";
        verifyNewClientWithHundredBux(email);
    }

    private void verifyNewClientWithHundredBux(String email)
    throws IOException, InterruptedException {
        httpRequestHelper.verifyCreateClient(email);
        HttpResponse<String> depositResponse = httpRequestHelper.deposit(email, HUNDRED_BUX);
        assertThat(depositResponse.statusCode()).isEqualTo(StatusCodes.OK);
        httpRequestHelper.verifyBalance(email, new Balance(HUNDRED_BUX));
    }

    @Test
    void deposit_negativeAmount() throws IOException, InterruptedException {
        final String email = "email6";
        httpRequestHelper.verifyCreateClient(email);
        HttpResponse<String> depositResponse = httpRequestHelper.deposit(email, new BigDecimal("-100.00"));
        assertThat(depositResponse.statusCode()).isEqualTo(StatusCodes.BAD_REQUEST);
    }

    @Test
    void deposit_ClientNotFound() throws IOException, InterruptedException {
        final String email = "nonExistingMail";
        HttpResponse<String> depositResponse = httpRequestHelper.deposit(email, HUNDRED_BUX);
        assertThat(depositResponse.statusCode()).isEqualTo(StatusCodes.BAD_REQUEST);
    }

    @Test
    void withdraw() throws IOException, InterruptedException {
        final String email = "email7";
        verifyNewClientWithHundredBux(email);
        HttpResponse<String> withdrawResponse = httpRequestHelper.withdraw(email, HUNDRED_BUX);
        assertThat(withdrawResponse.statusCode()).isEqualTo(StatusCodes.OK);
        httpRequestHelper.verifyBalance(email, new Balance(BigDecimal.ZERO));
    }

    @Test
    void withdraw_insufficientFunds() throws IOException, InterruptedException {
        final String email = "email8";
        verifyNewClientWithHundredBux(email);
        HttpResponse<String> withdrawResponse = httpRequestHelper.withdraw(email,
                                                                           new BigDecimal("10000000.00"));
        assertThat(withdrawResponse.statusCode()).isEqualTo(StatusCodes.BAD_REQUEST);
    }

    @Test
    void withdraw_negativeAmount() throws IOException, InterruptedException {
        final String email = "email9";
        verifyNewClientWithHundredBux(email);
        HttpResponse<String> withdrawResponse = httpRequestHelper.withdraw(email,
                                                                           new BigDecimal("-10000000.00"));
        assertThat(withdrawResponse.statusCode()).isEqualTo(StatusCodes.BAD_REQUEST);
    }

    @Test
    void withdraw_clientNotFound() throws IOException, InterruptedException {
        HttpResponse<String> withdrawResponse = httpRequestHelper.withdraw("notFoundClient", HUNDRED_BUX);
        assertThat(withdrawResponse.statusCode()).isEqualTo(StatusCodes.BAD_REQUEST);
    }

    @Test
    void transfer() throws IOException, InterruptedException {
        final String sender = "email10";
        final String receiver = "email11";
        httpRequestHelper.verifyCreateClient(receiver);
        verifyNewClientWithHundredBux(sender);
        HttpResponse<String> transferResponse = httpRequestHelper.transfer(sender, receiver, HUNDRED_BUX);
        assertThat(transferResponse.statusCode()).isEqualTo(StatusCodes.OK);
        httpRequestHelper.verifyBalance(sender, new Balance(BigDecimal.ZERO));
        httpRequestHelper.verifyBalance(receiver, new Balance(HUNDRED_BUX));
    }

    @Test
    void transfer_clientNotFound() throws IOException, InterruptedException {
        HttpResponse<String> transferResponse = httpRequestHelper.transfer("notFoundClient",
                                                                           "notFound",
                                                                           HUNDRED_BUX);
        assertThat(transferResponse.statusCode()).isEqualTo(StatusCodes.BAD_REQUEST);
    }

    @Test
    void transfer_negativeAmount() throws IOException, InterruptedException {
        final String sender = "email12";
        final String receiver = "email13";
        httpRequestHelper.verifyCreateClient(sender);
        httpRequestHelper.verifyCreateClient(receiver);
        HttpResponse<String> transferResponse = httpRequestHelper.transfer(sender,
                                                                           receiver,
                                                                           new BigDecimal("-1000.00"));
        assertThat(transferResponse.statusCode()).isEqualTo(StatusCodes.BAD_REQUEST);
    }

    @Test
    void transfer_insufficientFunds() throws IOException, InterruptedException {
        final String sender = "email14";
        final String receiver = "email15";
        httpRequestHelper.verifyCreateClient(sender);
        httpRequestHelper.verifyCreateClient(receiver);
        HttpResponse<String> transferResponse = httpRequestHelper.transfer(sender,
                                                                           receiver,
                                                                           new BigDecimal("1000.00"));
        assertThat(transferResponse.statusCode()).isEqualTo(StatusCodes.BAD_REQUEST);
    }


    /*private HttpResponse<String> transfer(String sender, String receiver, BigDecimal hundredBux)
    throws IOException, InterruptedException {
        return httpRequestHelper.transfer(sender, receiver, hundredBux);
    }

    private void verifyCreateClient(String email1) throws IOException, InterruptedException {
        httpRequestHelper.verifyCreateClient(email1);
    }

    private void verifyBalance(String email, Balance expectedBalance)
    throws IOException, InterruptedException {
        httpRequestHelper.verifyBalance(email, expectedBalance);
    }

    private HttpResponse<String> withdraw(String email, BigDecimal amount)
    throws IOException, InterruptedException {
        return httpRequestHelper.withdraw(email, amount);
    }


    private HttpResponse<String> deposit(String email, BigDecimal amount)
    throws IOException, InterruptedException {
        return httpRequestHelper.deposit(email, amount);
    }

    private HttpResponse<String> getBalanceResponse(String email)
    throws IOException, InterruptedException {
        return httpRequestHelper.getBalanceResponse(email);
    }

    private HttpResponse<String> sendRequest(HttpClient client, HttpRequest request)
    throws IOException, InterruptedException {
        return httpRequestHelper.sendRequest(client, request);
    }

    private HttpResponse<String> createClientResponse(String email)
    throws IOException, InterruptedException {

        return httpRequestHelper.createClientResponse(email);
    }*/
}
