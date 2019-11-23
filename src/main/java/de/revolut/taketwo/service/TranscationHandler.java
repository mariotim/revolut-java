package de.revolut.taketwo.service;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import lombok.NonNull;

public class TranscationHandler {


    public static void createUserHandler(
        @NonNull
        final HttpServerExchange exchange) {
        exchange.setStatusCode(StatusCodes.CREATED);

    }
}
