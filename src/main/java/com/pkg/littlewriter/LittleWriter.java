package com.pkg.littlewriter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
public class LittleWriter {
    public static void main(String[] args) {
        SpringApplication.run(LittleWriter.class, args);
    }
}
