package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Full end-to-end test against a running embedded server (RANDOM_PORT).
 *
 * Spring Boot auto-configures a WebTestClient bean when:
 * - webEnvironment = RANDOM_PORT (or DEFINED_PORT)
 * - spring-webflux is on the classpath
 *
 * This is the closest to a real HTTP call (real socket, real server).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "app.greeting=E2E Greeting"
})
class WebTestClientRandomPortTest {

    @Autowired
    private WebTestClient webTestClient;

    @LocalServerPort
    private int port;

    @Test
    void health_returnsUp() {
        webTestClient.get()
                .uri("/api/health")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.status").isEqualTo("UP");
    }

    @Test
    void greet_returnsMessageUsingTestProperties() {
        webTestClient.get()
                .uri("/api/greet/{name}", "Alice")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.message").isEqualTo("E2E Greeting, Alice!")
                .jsonPath("$.info").value(info -> {
                    String s = (String) info;
                    // from application-test.properties + @TestPropertySource
                    org.assertj.core.api.Assertions.assertThat(s)
                            .contains("env=test")
                            .contains("profiles=test")
                            .contains("property(app.greeting)=E2E Greeting");
                });
    }

    @Test
    void serverIsRunningOnRandomPort() {
        // Just to illustrate @LocalServerPort
        org.assertj.core.api.Assertions.assertThat(port).isGreaterThan(0);
    }
}
