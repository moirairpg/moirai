package es.thalesalv.gptbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class GptbotApplication {

	public static void main(String[] args) {
		SpringApplication.run(GptbotApplication.class, args);
	}
}
