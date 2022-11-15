package dev.pro.animalshelterbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AnimalShelterBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnimalShelterBotApplication.class, args);
	}

}
