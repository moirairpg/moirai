package es.thalesalv.chatrpg.application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class ObjectMapperConfiguration {

    @Bean
    public JsonFactory jsonFactory() {

        JsonFactory factory = new JsonFactory();
        factory.enable(JsonParser.Feature.ALLOW_COMMENTS);
        return factory;
    }

    @Bean
    public YAMLFactory yamlFactory() {

        YAMLFactory factory = new YAMLFactory();
        factory.enable(JsonParser.Feature.ALLOW_COMMENTS);
        return factory;
    }

    @Bean(name = "objectMapper")
    public ObjectMapper objectMapper() {

        return new ObjectMapper(jsonFactory()).setSerializationInclusion(Include.NON_EMPTY)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }

    @Bean(name = "yamlObjectMapper")
    public ObjectMapper yamlObjectMapper() {

        return new ObjectMapper(yamlFactory()).setSerializationInclusion(Include.NON_EMPTY)
                .setPropertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE);
    }

    @Bean(name = "prettyPrintObjectMapper")
    public ObjectWriter prettyPrintObjectMapper() {

        return new ObjectMapper(jsonFactory()).setSerializationInclusion(Include.NON_EMPTY)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .writerWithDefaultPrettyPrinter();
    }
}
