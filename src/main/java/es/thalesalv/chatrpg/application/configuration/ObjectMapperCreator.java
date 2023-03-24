package es.thalesalv.chatrpg.application.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

@Configuration
public class ObjectMapperCreator {

    @Bean(name = "objectMapper")
    public ObjectMapper objectMapper() {

        return new ObjectMapper();
    }

    @Bean(name = "yamlObjectMapper")
    public ObjectMapper yamlObjectMapper() {

        return new ObjectMapper(new YAMLFactory())
                .setSerializationInclusion(Include.NON_EMPTY)
                .setPropertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE);
    }

    @Bean(name = "prettyPrintObjectMapper")
    public ObjectWriter prettyPrintObjectMapper() {

        return new ObjectMapper()
                .setSerializationInclusion(Include.NON_EMPTY)
                .writerWithDefaultPrettyPrinter();
    }
}
