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
public class ModelSettingsDTOToEntity implements Function<ModelSettings, ModelSettingsEntity> {

    private final JDA jda;

    @Override
    public ModelSettingsEntity apply(ModelSettings modelSettings) {

        return ModelSettingsEntity.builder()
                .id(modelSettings.getId())
                .owner(Optional.ofNullable(modelSettings.getOwner())
                        .orElse(jda.getSelfUser()
                                .getId()))
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
