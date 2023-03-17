package es.thalesalv.chatrpg.application.config.newconfig;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import es.thalesalv.chatrpg.application.util.YamlPropertySourceFactory;
import lombok.Value;

@Value
@Configuration
@ConfigurationProperties
@PropertySource(value = "classpath:channel-config.yaml", factory = YamlPropertySourceFactory.class)
public class ChannelConfig {

}
