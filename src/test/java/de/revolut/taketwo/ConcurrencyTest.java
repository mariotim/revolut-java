package de.revolut.taketwo;

import de.revolut.taketwo.model.Balance;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.io.IOException;
import java.math.BigDecimal;

import io.undertow.util.StatusCodes;
import jdk.incubator.http.HttpClient;
import jdk.incubator.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

public class ConcurrencyTest {
    private static final int POOL_SIZE = 5;
    private static final int INVOCATION_COUNT = 1000;
    private final String EMAIL = "emailToTestConcurrency";
    private HttpRequestHelper httpRequestHelper;
    private EntryPoint entryPoint;


    @BeforeSuite
    void setup() throws IOException, InterruptedException {
        entryPoint = new EntryPoint();
        entryPoint.startServer();
        httpRequestHelper = new HttpRequestHelper(HttpClient.newHttpClient());
        httpRequestHelper.verifyNewClientWithHundredBux(EMAIL);
    }

    @org.testng.annotations.Test(threadPoolSize = POOL_SIZE,
                                 invocationCount = INVOCATION_COUNT)
    void multipleRequestsToWithdrawFromOneBankAccount() throws IOException, InterruptedException {
        HttpResponse<String> withdrawResponse = httpRequestHelper.withdraw(EMAIL,
                                                                           new BigDecimal("0.01"));
        assertThat(withdrawResponse.statusCode()).isEqualTo(StatusCodes.OK);

    }

    @Test(dependsOnMethods = {"multipleRequestsToWithdrawFromOneBankAccount"})
    void verifyBalance() throws IOException, InterruptedException {
        BigDecimal expectedBalanceAfterAll = new BigDecimal("90.00");
        httpRequestHelper.verifyBalance(EMAIL, new Balance(expectedBalanceAfterAll));
    }

    @AfterSuite
    void tearDown() {
        entryPoint.stopServer();
    }
}
