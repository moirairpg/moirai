package es.thalesalv.chatrpg.application.util.dbutils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.thalesalv.chatrpg.adapters.data.entity.NudgeEntity;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class NudgeConverter implements AttributeConverter<NudgeEntity, String> {

    @Override
    public String convertToDatabaseColumn(NudgeEntity attribute) {

        try {

            return new ObjectMapper().writeValueAsString(attribute);
        } catch (JsonProcessingException e) {

            throw new RuntimeException("Error parsing nudges to string from object");
        }
    }

    @Override
    public NudgeEntity convertToEntityAttribute(String dbData) {

        try {

            return new ObjectMapper().readValue(dbData, NudgeEntity.class);
        } catch (JsonProcessingException e) {

            throw new RuntimeException("Error parsing nudges to object from string");
        }
    }
}