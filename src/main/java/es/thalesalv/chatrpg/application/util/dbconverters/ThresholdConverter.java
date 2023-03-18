package es.thalesalv.chatrpg.application.util.dbconverters;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
@SuppressWarnings("unchecked")
public class ThresholdConverter implements AttributeConverter<Map<String, Double>, String> {

    @Override
    public String convertToDatabaseColumn(Map<String, Double> attribute) {

        try {
            return new ObjectMapper().writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing thresholds to string from map");
        }
    }

    @Override
    public Map<String, Double> convertToEntityAttribute(String dbData) {

        try {
            return new ObjectMapper().readValue(dbData, Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing thresholds to map from string");
        }
    }
}
