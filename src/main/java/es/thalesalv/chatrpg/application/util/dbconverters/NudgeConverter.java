package es.thalesalv.chatrpg.application.util.dbconverters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.thalesalv.chatrpg.adapters.data.db.entity.Nudge;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class NudgeConverter implements AttributeConverter<Nudge, String> {

    @Override
    public String convertToDatabaseColumn(Nudge attribute) {

        try {
            return new ObjectMapper().writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing nudges to string from object");
        }
    }

    @Override
    public Nudge convertToEntityAttribute(String dbData) {

        try {
            return new ObjectMapper().readValue(dbData, Nudge.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing nudges to object from string");
        }
    }
}