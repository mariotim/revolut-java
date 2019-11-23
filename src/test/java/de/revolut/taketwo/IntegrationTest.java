package de.revolut.taketwo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
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

    @BeforeEach
    void init() {
        entryPoint = new EntryPoint();
        entryPoint.startServer();

    }

    @Test
    void createClient() throws IOException, InterruptedException {
        final String email = "email";
        assertClientCreation(email);
    }

    @Test
    void balanceClient() {

    }

    private void assertClientCreation(String email) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest
                                  .newBuilder()
                                  .uri(URI.create(URI_CREATE + email))
                                  .PUT(HttpRequest.BodyPublisher.noBody())
                                  .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandler.asString());
        assertThat(response.statusCode()).isEqualTo(StatusCodes.CREATED);
    }

   /* @Test
    void test() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest
                                  .newBuilder()
                                  .uri(URI.create("http://localhost:8080/balance/test"))
                                  .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandler.asString());
        System.out.println(response);


    }*/
}
