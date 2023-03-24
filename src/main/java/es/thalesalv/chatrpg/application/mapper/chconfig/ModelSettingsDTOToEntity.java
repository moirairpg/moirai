package es.thalesalv.chatrpg.application.mapper.chconfig;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.db.entity.ModelSettingsEntity;
import es.thalesalv.chatrpg.domain.model.openai.dto.ModelSettings;

@Component
public class ModelSettingsDTOToEntity implements Function<ModelSettings, ModelSettingsEntity> {

    @Override
    public ModelSettingsEntity apply(ModelSettings t) {

        return ModelSettingsEntity.builder()
                .id(t.getId())
                .chatHistoryMemory(t.getChatHistoryMemory())
                .frequencyPenalty(t.getFrequencyPenalty())
                .logitBias(t.getLogitBias())
                .maxTokens(t.getMaxTokens())
                .modelName(t.getModelName())
                .owner(t.getOwner())
                .presencePenalty(t.getPresencePenalty())
                .stopSequence(t.getStopSequence())
                .temperature(t.getTemperature())
                .build();
    }
}
