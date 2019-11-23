package de.revolut.taketwo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IntegrationTest {
    private EntryPoint entryPoint;

    @BeforeEach
    void init() {
        entryPoint = new EntryPoint();
        entryPoint.startServer();
    }

    @Test
    void test() {
        
    }
}
