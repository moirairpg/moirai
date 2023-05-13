package es.thalesalv.chatrpg.application.util.dbutils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

    private static final String SPLIT_CHAR = ";";

    @Override
    public String convertToDatabaseColumn(List<String> stringList) {

        return Optional.ofNullable(stringList)
                .filter(a -> !a.isEmpty())
                .map(a -> {
                    return String.join(SPLIT_CHAR, stringList);
                })
                .orElse(null);
    }

    @Override
    public List<String> convertToEntityAttribute(String string) {

        return Optional.ofNullable(string)
                .filter(StringUtils::isNotBlank)
                .map(s -> {
                    return Arrays.asList(string.split(SPLIT_CHAR));
                })
                .orElse(null);
    }
}