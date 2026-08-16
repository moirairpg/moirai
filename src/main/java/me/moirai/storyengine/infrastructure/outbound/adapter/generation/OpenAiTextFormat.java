package me.moirai.storyengine.infrastructure.outbound.adapter.generation;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenAiTextFormat {

    @JsonProperty("type")
    private String type;

    @JsonProperty("name")
    private String name;

    @JsonProperty("strict")
    private Boolean strict;

    @JsonProperty("schema")
    private Map<String, Object> schema;

    public OpenAiTextFormat() {
    }

    public OpenAiTextFormat(String type, String name, Boolean strict, Map<String, Object> schema) {

        this.type = type;
        this.name = name;
        this.strict = strict;
        this.schema = schema;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Boolean getStrict() {
        return strict;
    }

    public Map<String, Object> getSchema() {
        return schema;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStrict(Boolean strict) {
        this.strict = strict;
    }

    public void setSchema(Map<String, Object> schema) {
        this.schema = schema;
    }
}
