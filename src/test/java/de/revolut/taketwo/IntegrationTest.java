package de.revolut.taketwo;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.revolut.taketwo.model.Balance;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;

import io.undertow.util.StatusCodes;
import jdk.incubator.http.HttpClient;
import jdk.incubator.http.HttpRequest;
import jdk.incubator.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IntegrationTest {
    private static final BigDecimal HUNDRED_BUX = new BigDecimal("100.00");
    private static final String URI_CREATE = "http://localhost:8080/client/";
    private static final String URI_BALANCE = "http://localhost:8080/balance/";
    private static final String URI_DEPOSIT = "http://localhost:8080/deposit/";
    private static final String URI_WITHDRAW = "http://localhost:8080/withdraw/";
    private static final String URI_TRANSFER = "http://localhost:8080/transfer/";
    private static EntryPoint entryPoint;
    private static HttpClient client;

    @BeforeAll
    static void init() {
        entryPoint = new EntryPoint();
        entryPoint.startServer();
        client = HttpClient.newHttpClient();
    }

    @AfterAll
    static void tearDown() {
        entryPoint.stopServer();
    }


    @Test
    void createClient() throws IOException, InterruptedException {
        final String email1 = "email1";
        verifyCreateClient(email1);
    }


    @Test
    void createClient_Exist() throws IOException, InterruptedException {
        verifyCreateClient("email2");
        HttpResponse<String> duplicateMail = createClientResponse("email2");
        assertThat(duplicateMail.statusCode()).isEqualTo(StatusCodes.CONFLICT);
    }

    @Test
    void balance() throws IOException, InterruptedException {
        final String email = "email3";
        verifyCreateClient(email);
        final Balance expectedBalance = new Balance(BigDecimal.ZERO);
        verifyBalance(email, expectedBalance);
    }

    @Test
    void balance_noClientFound() throws IOException, InterruptedException {
        final String email = "email4";
        HttpResponse<String> response = getBalanceResponse(email);
        assertThat(response.statusCode()).isEqualTo(StatusCodes.BAD_REQUEST);
    }

    @Test
    void deposit() throws IOException, InterruptedException {
        final String email = "email5";
        verifyDepositHundredBux(email);
    }

    private void verifyDepositHundredBux(String email) throws IOException, InterruptedException {
        verifyCreateClient(email);
        HttpResponse<String> depositResponse = deposit(email, HUNDRED_BUX);
        assertThat(depositResponse.statusCode()).isEqualTo(StatusCodes.OK);
        verifyBalance(email, new Balance(HUNDRED_BUX));
    }

    @Test
    void deposit_negativeAmount() throws IOException, InterruptedException {
        final String email = "email6";
        verifyCreateClient(email);
        HttpResponse<String> depositResponse = deposit(email, new BigDecimal("-100.00"));
        assertThat(depositResponse.statusCode()).isEqualTo(StatusCodes.BAD_REQUEST);
    }

    @Test
    void deposit_ClientNotFound() throws IOException, InterruptedException {
        final String email = "nonExistingMail";
        HttpResponse<String> depositResponse = deposit(email, HUNDRED_BUX);
        assertThat(depositResponse.statusCode()).isEqualTo(StatusCodes.BAD_REQUEST);
    }

    @Test
    void withdraw() throws IOException, InterruptedException {
        final String email = "email7";
        verifyDepositHundredBux(email);
        HttpResponse<String> withdrawResponse = withdraw(email, HUNDRED_BUX);
        assertThat(withdrawResponse.statusCode()).isEqualTo(StatusCodes.OK);
        verifyBalance(email, new Balance(BigDecimal.ZERO));
    }

    @Test
    void withdraw_insufficientFunds() throws IOException, InterruptedException {
        final String email = "email8";
        verifyDepositHundredBux(email);
        HttpResponse<String> withdrawResponse = withdraw(email, new BigDecimal("10000000.00"));
        assertThat(withdrawResponse.statusCode()).isEqualTo(StatusCodes.BAD_REQUEST);
    }

    @Test
    void withdraw_negativeAmount() throws IOException, InterruptedException {
        final String email = "email9";
        verifyDepositHundredBux(email);
        HttpResponse<String> withdrawResponse = withdraw(email, new BigDecimal("-10000000.00"));
        assertThat(withdrawResponse.statusCode()).isEqualTo(StatusCodes.BAD_REQUEST);
    }

    @Test
    void withdraw_clientNotFound() throws IOException, InterruptedException {
        HttpResponse<String> withdrawResponse = withdraw("notFoundClient", HUNDRED_BUX);
        assertThat(withdrawResponse.statusCode()).isEqualTo(StatusCodes.BAD_REQUEST);
    }

    private void verifyCreateClient(String email1) throws IOException, InterruptedException {
        HttpResponse<String> response = createClientResponse(email1);
        assertThat(response.statusCode()).isEqualTo(StatusCodes.CREATED);
    }

    private void verifyBalance(String email, Balance expectedBalance)
    throws IOException, InterruptedException {
        HttpResponse<String> response = getBalanceResponse(email);
        Balance balance = new ObjectMapper().readValue(response.body(), Balance.class);
        assertThat(response.statusCode()).isEqualTo(StatusCodes.OK);
        assertThat(balance).isEqualTo(expectedBalance);
    }

    private HttpResponse<String> withdraw(String email, BigDecimal amount)
    throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        HttpRequest request = builder
                                  .uri(URI.create(URI_WITHDRAW + email + "/" + amount))
                                  .POST(HttpRequest.BodyPublisher.noBody())
                                  .build();
        return sendRequest(client, request);
    }


    private HttpResponse<String> deposit(String email, BigDecimal amount)
    throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        HttpRequest request = builder
                                  .uri(URI.create(URI_DEPOSIT + email + "/" + amount))
                                  .POST(HttpRequest.BodyPublisher.noBody())
                                  .build();
        return sendRequest(client, request);
    }

    private HttpResponse<String> getBalanceResponse(String email)
    throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        HttpRequest request = builder
                                  .uri(URI.create(URI_BALANCE + email))
                                  .build();
        return sendRequest(client, request);
    }

    private HttpResponse<String> sendRequest(HttpClient client, HttpRequest request)
    throws IOException, InterruptedException {
        return client.send(request, HttpResponse.BodyHandler.asString());
    }

    private HttpResponse<String> createClientResponse(String email)
    throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        HttpRequest request = builder
                                  .uri(URI.create(URI_CREATE + email))
                                  .PUT(HttpRequest.BodyPublisher.noBody())
                                  .build();

        return sendRequest(client, request);
    }
}
