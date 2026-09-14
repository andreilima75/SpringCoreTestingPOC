package com.example.demo;

import com.example.demo.config.AppProperties;
import com.example.demo.controller.GreetingController;
import com.example.demo.service.GreetingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Lightweight unit-style test: bind WebTestClient directly to a controller
 * (no full ApplicationContext, no server).
 *
 * Useful when you want to test controller + service logic in isolation
 * while still using the same fluent WebTestClient API.
 */
class WebTestClientBindToControllerTest {

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        MockEnvironment env = new MockEnvironment();
        env.setProperty("app.greeting", "Standalone");
        env.setProperty("app.environment-name", "unit");
        env.setProperty("app.max-items", "3");
        env.setActiveProfiles("unit");

        AppProperties props = new AppProperties();
        props.setGreeting("Standalone");
        props.setEnvironmentName("unit");
        props.setMaxItems(3);

        GreetingService service = new GreetingService(env, props);
        GreetingController controller = new GreetingController(service);

        // Bind only the controller (no Spring context needed)
        webTestClient = WebTestClient.bindToController(controller).build();
    }

    @Test
    void greet_withStandaloneController() {
        webTestClient.get()
                .uri("/api/greet/Charlie")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Standalone, Charlie!")
                .jsonPath("$.info").value(info ->
                        org.assertj.core.api.Assertions.assertThat((String) info)
                                .contains("env=unit")
                                .contains("maxItems=3")
                );
    }

    @Test
    void health_withStandaloneController() {
        webTestClient.get()
                .uri("/api/health")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("UP");
    }
}
