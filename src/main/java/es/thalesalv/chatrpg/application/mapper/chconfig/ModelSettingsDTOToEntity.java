package es.thalesalv.chatrpg.application.mapper.chconfig;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.entity.ModelSettingsEntity;
import es.thalesalv.chatrpg.domain.model.chconf.ModelSettings;

@Component
public class ModelSettingsDTOToEntity implements Function<ModelSettings, ModelSettingsEntity> {

    @Override
    public ModelSettingsEntity apply(ModelSettings modelSettings) {

        return ModelSettingsEntity.builder()
                .id(modelSettings.getId())
                .owner(modelSettings.getOwner())
                .logitBias(modelSettings.getLogitBias())
                .maxTokens(modelSettings.getMaxTokens())
                .modelName(modelSettings.getModelName())
                .temperature(modelSettings.getTemperature())
                .stopSequence(modelSettings.getStopSequence())
                .presencePenalty(modelSettings.getPresencePenalty())
                .frequencyPenalty(modelSettings.getFrequencyPenalty())
                .chatHistoryMemory(modelSettings.getChatHistoryMemory())
                .build();
    }
}
