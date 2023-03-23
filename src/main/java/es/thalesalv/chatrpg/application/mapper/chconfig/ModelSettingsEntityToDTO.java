package es.thalesalv.chatrpg.application.mapper.chconfig;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.db.entity.ModelSettingsEntity;
import es.thalesalv.chatrpg.domain.model.openai.dto.ModelSettings;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ModelSettingsEntityToDTO implements Function<ModelSettingsEntity, ModelSettings> {

    @Override
    public ModelSettings apply(ModelSettingsEntity t) {

        return ModelSettings.builder()
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
