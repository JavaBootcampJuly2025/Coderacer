package com.coderacer.runner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RunnerApp {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(RunnerApp.class);
        app.setAdditionalProfiles("runner");
        app.run(args);
    }
}
