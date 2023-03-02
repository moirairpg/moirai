package es.thalesalv.gptbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableAutoConfiguration
@EnableConfigurationProperties
@EnableMongoRepositories
public class GptbotApplication {

	public static void main(String[] args) {
		SpringApplication.run(GptbotApplication.class, args);
	}
}
