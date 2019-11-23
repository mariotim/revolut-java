package de.revolut.taketwo;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.revolut.taketwo.model.Balance;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;

import io.undertow.util.StatusCodes;
import jdk.incubator.http.HttpClient;
import jdk.incubator.http.HttpRequest;
import jdk.incubator.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

public class IntegrationTest {
    private static final String URI_CREATE = "http://localhost:8080/client/";
    private static final String URI_BALANCE = "http://localhost:8080/balance/";
    private static final String URI_DEPOSIT = "http://localhost:8080/deposit/";
    private static final String URI_WITHDRAW = "http://localhost:8080/withdraw/";
    private static final String URI_TRANSFER = "http://localhost:8080/transfer/";
    private EntryPoint entryPoint;
    private HttpClient client;

    @BeforeEach
    void init() {
        entryPoint = new EntryPoint();
        entryPoint.startServer();
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    void tearDown() {
        entryPoint.stopServer();
    }


    @Test
    void createClient() throws IOException, InterruptedException {
        HttpResponse<String> response = createClientRequest("email1");
        assertThat(response.statusCode()).isEqualTo(StatusCodes.CREATED);
    }

    @Test
    void createClient_Exist() throws IOException, InterruptedException {
        HttpResponse<String> response = createClientRequest("email2");
        assertThat(response.statusCode()).isEqualTo(StatusCodes.CREATED);
        HttpResponse<String> duplicateMail = createClientRequest("email2");
        assertThat(duplicateMail.statusCode()).isEqualTo(StatusCodes.CONFLICT);
    }

    @Test
    void balance() throws IOException, InterruptedException {
        HttpResponse<String> createResponse = createClientRequest("email3");
        assertThat(createResponse.statusCode()).isEqualTo(StatusCodes.CREATED);
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        HttpRequest request = builder
                                  .uri(URI.create(URI_BALANCE + "email3"))
                                  .build();
        HttpResponse<String> response = sendRequest(client, request);
        Balance balance = new ObjectMapper().readValue(response.body(), Balance.class);
        assertThat(response.statusCode()).isEqualTo(StatusCodes.OK);
        assertThat(balance).isEqualTo(new Balance(new BigDecimal("0.00")));
    }

    private HttpResponse<String> sendRequest(HttpClient client, HttpRequest request)
    throws IOException, InterruptedException {
        return client.send(request, HttpResponse.BodyHandler.asString());
    }

    private HttpResponse<String> createClientRequest(String email)
    throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        HttpRequest request = builder
                                  .uri(URI.create(URI_CREATE + email))
                                  .PUT(HttpRequest.BodyPublisher.noBody())
                                  .build();

        return sendRequest(client, request);
    }
}
