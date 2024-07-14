package me.moirai.discordbot.core.domain.channelconfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import me.moirai.discordbot.common.exception.AIModelNotSupportedException;

public class ArtificialIntelligenceModelTest {

    @Test
    public void retrieveModelFromName() {

        // Given
        String internalModelName = "gpt35-16k";

        // When
        ArtificialIntelligenceModel model = ArtificialIntelligenceModel.findByInternalModelName(internalModelName);

        // Then
        assertThat(model).isNotNull()
                .hasToString(internalModelName);
    }

    @Test
    public void errorWhenModelNotSupported() {

        // Given
        String modelName = "new_model_super_turbo";

        // Then
        assertThrows(AIModelNotSupportedException.class,
                () -> ArtificialIntelligenceModel.findByInternalModelName(modelName));
    }
}
