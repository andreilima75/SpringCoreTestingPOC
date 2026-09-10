package com.example.demo;

import com.example.demo.config.AppProperties;
import com.example.demo.service.GreetingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Demonstrates Spring Environment abstraction + property source precedence.
 *
 * Precedence (highest first):
 * 1. @TestPropertySource (inlined properties > locations)
 * 2. application-{profile}.properties
 * 3. application.properties
 */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(
        locations = "classpath:test-override.properties",
        properties = {
                "app.greeting=Inline override wins",
                "custom.test.flag=true"
        }
)
class EnvironmentAndPropertiesTest {

    @Autowired
    private Environment environment;

    @Autowired
    private AppProperties appProperties;

    @Autowired
    private GreetingService greetingService;

    @Test
    void environment_resolvesPropertiesWithCorrectPrecedence() {
        // Inlined @TestPropertySource has highest precedence
        assertThat(environment.getProperty("app.greeting"))
                .isEqualTo("Inline override wins");

        // From test-override.properties (locations)
        assertThat(environment.getProperty("app.max-items"))
                .isEqualTo("99");

        // From application-test.properties (profile)
        assertThat(environment.getProperty("app.environment-name"))
                .isEqualTo("test");

        // Custom inlined property
        assertThat(environment.getProperty("custom.test.flag", Boolean.class))
                .isTrue();
    }

    @Test
    void configurationProperties_areBoundFromEnvironment() {
        assertThat(appProperties.getGreeting()).isEqualTo("Inline override wins");
        assertThat(appProperties.getMaxItems()).isEqualTo(99);
        assertThat(appProperties.getEnvironmentName()).isEqualTo("test");
    }

    @Test
    void greetingService_usesOverriddenProperties() {
        assertThat(greetingService.greet("World"))
                .isEqualTo("Inline override wins, World!");

        String info = greetingService.getActiveEnvironmentInfo();
        assertThat(info)
                .contains("env=test")
                .contains("profiles=test")
                .contains("maxItems=99")
                .contains("property(app.greeting)=Inline override wins");
    }

    @Test
    void activeProfiles_areVisibleOnEnvironment() {
        assertThat(environment.getActiveProfiles()).contains("test");
    }
}
