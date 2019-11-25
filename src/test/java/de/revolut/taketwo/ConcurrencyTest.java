package de.revolut.taketwo;

import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;

import jdk.incubator.http.HttpClient;

public class ConcurrencyTest {
    public static final String EMAIL = "email16";
    private static EntryPoint entryPoint;
    private static HttpClient client;

    @BeforeAll
    static void init() throws IOException, InterruptedException {
        entryPoint = new EntryPoint();
        entryPoint.startServer();
        client = HttpClient.newHttpClient();
    }

    @org.testng.annotations.Test(threadPoolSize = 2,
                                 invocationCount = 10)
    void multipleRequestsToWithdrawFromOneBankAccount() throws IOException, InterruptedException {
//        HttpResponse<String> depositResponse = deposit(email, HUNDRED_BUX);
//        assertThat(depositResponse.statusCode()).isEqualTo(StatusCodes.OK);
//
//
//        System.out.println(withdrawResponse.body());
        //        verifyBalance(email, new Balance(BigDecimal.ZERO));
    }

}
