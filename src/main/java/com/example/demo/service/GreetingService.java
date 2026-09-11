package com.example.demo.service;

import com.example.demo.config.AppProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 * Uses both Environment abstraction and typed @ConfigurationProperties.
 */
@Service
public class GreetingService {

    private final Environment environment;
    private final AppProperties appProperties;

    public GreetingService(Environment environment, AppProperties appProperties) {
        this.environment = environment;
        this.appProperties = appProperties;
    }

    public String greet(String name) {
        String greeting = appProperties.getGreeting();
        return greeting + ", " + name + "!";
    }

    public String getActiveEnvironmentInfo() {
        String[] activeProfiles = environment.getActiveProfiles();
        String profiles = activeProfiles.length == 0
                ? "default"
                : String.join(",", activeProfiles);

        return "env=" + appProperties.getEnvironmentName()
                + ", profiles=" + profiles
                + ", maxItems=" + appProperties.getMaxItems()
                + ", property(app.greeting)=" + environment.getProperty("app.greeting");
    }
}
