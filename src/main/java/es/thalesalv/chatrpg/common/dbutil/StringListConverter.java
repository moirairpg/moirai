package es.thalesalv.chatrpg.common.dbutil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

    private static final String SPLIT_CHAR = ";";

    @Override
    public String convertToDatabaseColumn(List<String> stringList) {

        if (stringList != null && !stringList.isEmpty()) {
            return String.join(SPLIT_CHAR, stringList);
        }

        return null;
    }

    @Override
    public List<String> convertToEntityAttribute(String string) {

        if (StringUtils.isNotBlank(string)) {
            return Arrays.asList(string.split(SPLIT_CHAR));
        }

        return Collections.emptyList();
    }
}