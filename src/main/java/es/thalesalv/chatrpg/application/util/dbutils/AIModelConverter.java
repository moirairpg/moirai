package es.thalesalv.chatrpg.application.util.dbutils;

import es.thalesalv.chatrpg.domain.enums.AIModel;
import jakarta.persistence.AttributeConverter;

public class AIModelConverter implements AttributeConverter<AIModel, String> {

    @Override
    public String convertToDatabaseColumn(AIModel aiModel) {

        return aiModel.getInternalName();
    }

    @Override
    public AIModel convertToEntityAttribute(String internalName) {

        return AIModel.findByInternalName(internalName);
    }
}