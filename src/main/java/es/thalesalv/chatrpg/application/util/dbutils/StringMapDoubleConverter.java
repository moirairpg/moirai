package es.thalesalv.chatrpg.application.util.dbutils;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
@SuppressWarnings("unchecked")
public class StringMapDoubleConverter implements AttributeConverter<Map<String, Double>, String> {

    @Override
    public String convertToDatabaseColumn(Map<String, Double> attribute) {

        try {
            return new ObjectMapper().writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing string from map");
        }
    }

    @Override
    public Map<String, Double> convertToEntityAttribute(String dbData) {

        try {
            return new ObjectMapper().readValue(dbData, Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing map from string");
        }
    }
}
