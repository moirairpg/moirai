package es.thalesalv.chatrpg.common.dbutil;

import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
@SuppressWarnings("unchecked")
public class StringMapDoubleConverter implements AttributeConverter<Map<String, Double>, String> {

    @Override
    public String convertToDatabaseColumn(Map<String, Double> attribute) {

        return Optional.ofNullable(attribute)
                .filter(a -> !a.isEmpty())
                .map(a -> {
                    try {
                        return new ObjectMapper().writeValueAsString(attribute);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException("Error parsing string from map");
                    }
                })
                .orElse(null);
    }

    @Override
    public Map<String, Double> convertToEntityAttribute(String dbData) {

        return Optional.ofNullable(dbData)
                .filter(StringUtils::isNotBlank)
                .map(s -> {
                    try {
                        return new ObjectMapper().readValue(dbData, Map.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException("Error parsing map from string");
                    }
                })
                .orElse(null);
    }
}
