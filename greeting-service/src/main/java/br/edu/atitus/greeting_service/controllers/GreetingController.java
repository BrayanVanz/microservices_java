package br.edu.atitus.greeting_service.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.edu.atitus.greeting_service.configs.GreetingConfig;

@RestController
@RequestMapping("/greeting")
public class GreetingController {

/*  @Value("${greeting-service.greeting}")
    private String greeting;

    @Value("${greeting-service.default-name}")
    private String defaultName; */

    private final GreetingConfig config;

    public GreetingController(GreetingConfig config) {
        this.config = config;
    }

    @GetMapping({"", "/"})
    public String getGreeting(@RequestParam(required = false) String name) {

        if (name == null || name.isEmpty()) {
            name = config.getDefaultName();
        }

        String greetingReturn = String.format("%s %s!!!", config.getGreeting(), name);
        return greetingReturn;
    }
}
