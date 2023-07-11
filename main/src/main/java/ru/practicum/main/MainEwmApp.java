package ru.practicum.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"ru.practicum.stats", "ru.practicum.main"})
public class MainEwmApp {
    public static void main(String[] args) {
        SpringApplication.run(MainEwmApp.class);
    }
}