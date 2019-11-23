package de.revolut.taketwo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.revolut.taketwo.db.BankDao;
import de.revolut.taketwo.model.Balance;
import de.revolut.taketwo.model.Client;

import java.nio.ByteBuffer;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;

public class TransactionHandler {
    private static final String APPLICATION_JSON = "application/json";
    private static final BankDao bank = BankDao.getInstance();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void createClientHandler(final HttpServerExchange exchange) {
        try {
            String email = extractParam(exchange, "email");
            bank.createClient(new Client(email));
            exchange.setStatusCode(StatusCodes.CREATED);
        } catch (BankDao.ClientAlreadyExist ex) {
            exchange.setStatusCode(StatusCodes.CONFLICT);
        } catch (Exception ex) {
            exchange.setStatusCode(StatusCodes.BAD_REQUEST);
        }
        exchange.endExchange();
    }

    public static void getBalance(final HttpServerExchange exchange) {
        try {
            String email = extractParam(exchange, "email");
            Balance balance = bank.balance(new Client(email));
            exchange.setStatusCode(StatusCodes.OK);
            respondWith(exchange, balance);
        } catch (BankDao.ClientAlreadyExist ex) {
            exchange.setStatusCode(StatusCodes.CONFLICT);
        } catch (Exception ex) {
            exchange.setStatusCode(StatusCodes.BAD_REQUEST);
        }
        exchange.endExchange();
    }

    private static void respondWith(HttpServerExchange exchange, Object object)
    throws JsonProcessingException {
        exchange
            .getResponseHeaders()
            .put(Headers.CONTENT_TYPE, APPLICATION_JSON);
        exchange
            .getResponseSender()
            .send(ByteBuffer.wrap(objectMapper.writeValueAsBytes(object)));
    }

    private static String extractParam(HttpServerExchange exchange, String email) {
        return exchange
                   .getQueryParameters()
                   .get(email)
                   .getFirst();
    }
}