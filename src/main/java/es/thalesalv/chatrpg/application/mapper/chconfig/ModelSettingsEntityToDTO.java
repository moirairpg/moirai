package es.thalesalv.chatrpg.application.mapper.chconfig;

import java.util.Optional;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.entity.ModelSettingsEntity;
import es.thalesalv.chatrpg.domain.model.chconf.ModelSettings;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;

@Component
@RequiredArgsConstructor
public class ModelSettingsEntityToDTO implements Function<ModelSettingsEntity, ModelSettings> {

    private final JDA jda;

    @Override
    public ModelSettings apply(ModelSettingsEntity modelSettingsEntity) {

        return ModelSettings.builder()
                .id(modelSettingsEntity.getId())
                .owner(Optional.ofNullable(modelSettingsEntity.getOwner())
                        .orElse(jda.getSelfUser()
                                .getId()))
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
