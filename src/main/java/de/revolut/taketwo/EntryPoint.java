package de.revolut.taketwo;

import de.revolut.taketwo.service.TranscationHandler;

import io.undertow.Undertow;
import io.undertow.server.RoutingHandler;

import static io.undertow.server.handlers.ResponseCodeHandler.HANDLE_404;

public class EntryPoint {
    private Undertow undertow;

    EntryPoint() {
        final RoutingHandler rootHandler = setupRoutingHandler();
        undertow = Undertow
                       .builder()
                       .addHttpListener(8080, "0.0.0.0", rootHandler)
                       .build();
    }

    private static RoutingHandler setupRoutingHandler() {
        final RoutingHandler rootHandler = new RoutingHandler();
        rootHandler.put("/client/{email}", TranscationHandler::createUserHandler);
        rootHandler.get("/balance/{email}", TranscationHandler::createUserHandler);
        rootHandler.post("/deposit/{email}/{amount}", null);
        rootHandler.post("/withdraw/{email}/{amount}", null);
        rootHandler.post("/transfer/{sender}/{receiver}/{amount}", null);
        rootHandler.setFallbackHandler(HANDLE_404);
        return rootHandler;
    }

    public static void main(String[] args) {
        new EntryPoint().startServer();
    }

    public void startServer() {
        undertow.start();
    }
}
