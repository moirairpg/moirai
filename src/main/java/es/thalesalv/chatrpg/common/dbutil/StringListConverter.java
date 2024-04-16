package es.thalesalv.chatrpg.common.dbutil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

    private static final String SPLIT_CHAR = ",";

    @Override
    public String convertToDatabaseColumn(List<String> inputList) {

        return ListUtils.emptyIfNull(inputList)
                .stream()
                .collect(Collectors.joining(SPLIT_CHAR));
    }

    @Override
    public List<String> convertToEntityAttribute(String inputString) {

        if (StringUtils.isBlank(inputString)) {
            return Collections.emptyList();
        }

        return Arrays.asList(inputString.split(SPLIT_CHAR));
    }
}