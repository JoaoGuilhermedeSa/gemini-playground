package com.example.charactercreation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CharacterCreationApplication {

    public static void main(String[] args) {
        SpringApplication.run(CharacterCreationApplication.class, args);
    }

}
