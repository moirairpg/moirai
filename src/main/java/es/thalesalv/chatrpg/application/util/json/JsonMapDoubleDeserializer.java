package es.thalesalv.chatrpg.application.util.json;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

@SuppressWarnings("unchecked")
public class JsonMapDoubleDeserializer extends JsonDeserializer<Map<String, String>> {

    @Override
    public Map<String, String> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {

        final Map<String, Double> doubleValues = p.readValueAs(Map.class);
        return doubleValues.entrySet().stream()
                .map(entry -> {
                    final String fixedValue = String.format("%.5f", entry.getValue());
                    return new AbstractMap.SimpleEntry<String, String>(entry.getKey(), fixedValue);
                })
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
    }
}