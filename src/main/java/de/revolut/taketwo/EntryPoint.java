package de.revolut.taketwo;

import io.undertow.Undertow;
import io.undertow.server.RoutingHandler;

import static io.undertow.server.handlers.ResponseCodeHandler.HANDLE_404;

public class EntryPoint {

    public static void main(String[] args) {
        final RoutingHandler rootHandler = new RoutingHandler();
        rootHandler.put("/client/{email}", null);
        rootHandler.get("/balance/{email}", null);
        rootHandler.post("/deposit/{email}/{amount}", null);
        rootHandler.post("/withdraw/{email}/{amount}", null);
        rootHandler.post("/transfer/{sender}/{receiver}/{amount}", null);
        rootHandler.setFallbackHandler(HANDLE_404);
        final Undertow undertow = Undertow
                                      .builder()
                                      .addHttpListener(8080, "localhost", rootHandler)
                                      .build();
        undertow.start();
    }
}
