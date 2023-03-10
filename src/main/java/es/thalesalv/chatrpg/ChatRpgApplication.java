package es.thalesalv.chatrpg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableAutoConfiguration
@EnableConfigurationProperties
public class ChatRpgApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatRpgApplication.class, args);
	}
}
