package com.mongo.challenge.kitchensink;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class KitchenSinkApplicationTests {

    @LocalServerPort
    private int port;


    @Test
    void contextLoads() {
        // Verify the application context loads successfully
        assertThat(port).isGreaterThan(0);
    }

}
