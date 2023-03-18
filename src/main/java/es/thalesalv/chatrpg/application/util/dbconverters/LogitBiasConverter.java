package es.thalesalv.chatrpg.application.util.dbconverters;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
@SuppressWarnings("unchecked")
public class LogitBiasConverter implements AttributeConverter<Map<String, Integer>, String> {

    @Override
    public String convertToDatabaseColumn(Map<String, Integer> attribute) {

        try {
            return new ObjectMapper().writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing logit biases to map from string");
        }
    }

    @Override
    public Map<String, Integer> convertToEntityAttribute(String dbData) {

        try {
            return new ObjectMapper().readValue(dbData, Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing logit biases to map from string");
        }
    }
}
