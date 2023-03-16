package es.thalesalv.chatrpg.application.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import es.thalesalv.chatrpg.application.util.YamlPropertySourceFactory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties
@PropertySource(value = "classpath:bot-config.yaml", factory = YamlPropertySourceFactory.class)
public class BotConfig {
    private List<Persona> personas;
    private String statusChannelId;
}