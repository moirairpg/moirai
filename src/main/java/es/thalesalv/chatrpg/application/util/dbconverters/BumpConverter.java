package es.thalesalv.chatrpg.application.util.dbconverters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.thalesalv.chatrpg.adapters.data.db.entity.Bump;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class BumpConverter implements AttributeConverter<Bump, String> {

    @Override
    public String convertToDatabaseColumn(Bump attribute) {

        try {
            return new ObjectMapper().writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing bumps to string from object");
        }
    }

    @Override
    public Bump convertToEntityAttribute(String dbData) {

        try {
            return new ObjectMapper().readValue(dbData, Bump.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing bumps to object from string");
        }
    }
}