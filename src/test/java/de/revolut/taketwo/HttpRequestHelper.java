package de.revolut.taketwo;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.revolut.taketwo.model.Balance;

import org.assertj.core.api.Assertions;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;

import io.undertow.util.StatusCodes;
import jdk.incubator.http.HttpClient;
import jdk.incubator.http.HttpRequest;
import jdk.incubator.http.HttpResponse;

public class HttpRequestHelper {
    private static final String URI_CREATE = "http://localhost:8080/client/";
    private static final String URI_BALANCE = "http://localhost:8080/balance/";
    private static final String URI_DEPOSIT = "http://localhost:8080/deposit/";
    private static final String URI_WITHDRAW = "http://localhost:8080/withdraw/";
    private static final String URI_TRANSFER = "http://localhost:8080/transfer/";
    private static HttpClient client;

    public HttpRequestHelper(HttpClient client) {
        this.client = client;
    }

    HttpResponse<String> transfer(String sender, String receiver, BigDecimal hundredBux)
    throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        HttpRequest request = builder
                                  .uri(URI.create(URI_TRANSFER
                                                  + sender
                                                  + "/"
                                                  + receiver
                                                  + "/"
                                                  + hundredBux))
                                  .POST(HttpRequest.BodyPublisher.noBody())
                                  .build();
        return sendRequest(client, request);
    }

    void verifyCreateClient(String email1) throws IOException, InterruptedException {
        HttpResponse<String> response = createClientResponse(email1);
        Assertions
            .assertThat(response.statusCode())
            .isEqualTo(StatusCodes.CREATED);
    }

    void verifyBalance(String email, Balance expectedBalance)
    throws IOException, InterruptedException {
        HttpResponse<String> response = getBalanceResponse(email);
        Balance balance = new ObjectMapper().readValue(response.body(), Balance.class);
        Assertions
            .assertThat(response.statusCode())
            .isEqualTo(StatusCodes.OK);
        Assertions
            .assertThat(balance)
            .isEqualTo(expectedBalance);
    }

    HttpResponse<String> withdraw(String email, BigDecimal amount)
    throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        HttpRequest request = builder
                                  .uri(URI.create(URI_WITHDRAW + email + "/" + amount))
                                  .POST(HttpRequest.BodyPublisher.noBody())
                                  .build();
        return sendRequest(client, request);
    }

    HttpResponse<String> deposit(String email, BigDecimal amount)
    throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        HttpRequest request = builder
                                  .uri(URI.create(URI_DEPOSIT + email + "/" + amount))
                                  .POST(HttpRequest.BodyPublisher.noBody())
                                  .build();
        return sendRequest(client, request);
    }

    HttpResponse<String> getBalanceResponse(String email) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        HttpRequest request = builder
                                  .uri(URI.create(URI_BALANCE + email))
                                  .build();
        return sendRequest(client, request);
    }

    HttpResponse<String> sendRequest(HttpClient client, HttpRequest request)
    throws IOException, InterruptedException {
        return client.send(request, HttpResponse.BodyHandler.asString());
    }

    HttpResponse<String> createClientResponse(String email)
    throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        HttpRequest request = builder
                                  .uri(URI.create(URI_CREATE + email))
                                  .PUT(HttpRequest.BodyPublisher.noBody())
                                  .build();

        return sendRequest(client, request);
    }
}
