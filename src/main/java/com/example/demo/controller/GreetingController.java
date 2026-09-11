package com.example.demo.controller;

import com.example.demo.service.GreetingService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class GreetingController {

    private final GreetingService greetingService;

    public GreetingController(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    @GetMapping(value = "/greet/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> greet(@PathVariable String name) {
        return Map.of(
                "message", greetingService.greet(name),
                "info", greetingService.getActiveEnvironmentInfo()
        );
    }

    @GetMapping(value = "/health", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> health() {
        return Map.of("status", "UP");
    }
}
