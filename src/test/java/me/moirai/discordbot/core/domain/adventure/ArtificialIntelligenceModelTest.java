package me.moirai.discordbot.core.domain.adventure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import me.moirai.discordbot.common.exception.AIModelNotSupportedException;

public class ArtificialIntelligenceModelTest {

    @Test
    public void retrieveModelFromName() {

        // Given
        String internalModelName = "gpt4-omni";
        String fullModelName = "GPT-4 Omni";
        String officialModelName = "gpt-4o";
        int hardTokenLimit = 128000;

        // When
        ArtificialIntelligenceModel model = ArtificialIntelligenceModel.fromInternalName(internalModelName);

        // Then
        assertThat(model).isNotNull()
                .hasToString(internalModelName);

        assertThat(model.getFullModelName()).isEqualTo(fullModelName);
        assertThat(model.getInternalModelName()).isEqualTo(internalModelName);
        assertThat(model.getOfficialModelName()).isEqualTo(officialModelName);
        assertThat(model.getHardTokenLimit()).isEqualTo(hardTokenLimit);
    }

    @Test
    public void errorWhenModelNotSupported() {

        // Given
        String modelName = "new_model_super_turbo";

        // Then
        assertThrows(AIModelNotSupportedException.class,
                () -> ArtificialIntelligenceModel.fromInternalName(modelName));
    }
}
