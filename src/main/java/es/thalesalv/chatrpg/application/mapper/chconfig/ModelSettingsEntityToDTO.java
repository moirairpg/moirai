package es.thalesalv.chatrpg.application.mapper.chconfig;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.entity.ModelSettingsEntity;
import es.thalesalv.chatrpg.domain.model.chconf.ModelSettings;

@Component
public class ModelSettingsEntityToDTO implements Function<ModelSettingsEntity, ModelSettings> {

    @Override
    public ModelSettings apply(ModelSettingsEntity modelSettingsEntity) {

        return ModelSettings.builder()
                .id(modelSettingsEntity.getId())
                .owner(modelSettingsEntity.getOwner())
                .logitBias(modelSettingsEntity.getLogitBias())
                .maxTokens(modelSettingsEntity.getMaxTokens())
                .modelName(modelSettingsEntity.getModelName())
                .temperature(modelSettingsEntity.getTemperature())
                .stopSequence(modelSettingsEntity.getStopSequence())
                .presencePenalty(modelSettingsEntity.getPresencePenalty())
                .frequencyPenalty(modelSettingsEntity.getFrequencyPenalty())
                .chatHistoryMemory(modelSettingsEntity.getChatHistoryMemory())
                .build();
    }
}
