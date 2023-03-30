package es.thalesalv.chatrpg.application.util.dbutils;

import es.thalesalv.chatrpg.domain.enums.Intent;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class IntentConverter implements AttributeConverter<Intent, String> {

    @Override
    public String convertToDatabaseColumn(Intent intent) {

        return intent.toString();
    }

    @Override
    public Intent convertToEntityAttribute(String text) {

        return Intent.fromString(text);
    }
}