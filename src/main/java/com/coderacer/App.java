package com.coderacer;

import com.coderacer.runner.RunnerApp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class App {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(RunnerApp.class);
        app.setAdditionalProfiles("app");
        app.run(args);
    }
}
