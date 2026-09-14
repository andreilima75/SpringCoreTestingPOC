package com.example.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.web.context.WebApplicationContext;

/**
 * Mock environment test (no real HTTP server).
 *
 * Uses MockMvcWebTestClient to bind WebTestClient to the Spring MVC
 * WebApplicationContext. Requests are handled via MockMvc (no socket).
 *
 * Faster than RANDOM_PORT while still exercising controllers, services,
 * and the Environment / property sources.
 */
@SpringBootTest  // default webEnvironment = MOCK
@ActiveProfiles("test")
@TestPropertySource(properties = "app.greeting=Mock Env Greeting")
class WebTestClientMockEnvironmentTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        // MVC-aware binding: delegates to MockMvc under the hood
        webTestClient = MockMvcWebTestClient.bindToApplicationContext(webApplicationContext).build();
    }

    @Test
    void greet_worksInMockEnvironment() {
        webTestClient.get()
                .uri("/api/greet/Bob")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Mock Env Greeting, Bob!")
                .jsonPath("$.info").value(info ->
                        org.assertj.core.api.Assertions.assertThat((String) info)
                                .contains("env=test")
                                .contains("property(app.greeting)=Mock Env Greeting")
                );
    }

    @Test
    void health_worksInMockEnvironment() {
        webTestClient.get()
                .uri("/api/health")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("UP");
    }
}
