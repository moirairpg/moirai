package es.thalesalv.chatrpg.application.util.dbutils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.thalesalv.chatrpg.adapters.data.db.entity.BumpEntity;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class BumpConverter implements AttributeConverter<BumpEntity, String> {

    @Override
    public String convertToDatabaseColumn(BumpEntity attribute) {

        try {
            return new ObjectMapper().writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing bumps to string from object");
        }
    }

    @Override
    public BumpEntity convertToEntityAttribute(String dbData) {

        try {
            return new ObjectMapper().readValue(dbData, BumpEntity.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing bumps to object from string");
        }
    }
}