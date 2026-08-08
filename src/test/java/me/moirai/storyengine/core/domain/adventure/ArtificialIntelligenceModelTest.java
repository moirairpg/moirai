package me.moirai.storyengine.core.domain.adventure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import me.moirai.storyengine.common.enums.ArtificialIntelligenceModel;
import me.moirai.storyengine.common.exception.UnsupportedAiModelException;

public class ArtificialIntelligenceModelTest {

    @Test
    public void retrieveModelFromName() {

        // Given
        String internalModelName = "GPT54";
        String fullModelName = "GPT-5.4";
        String officialModelName = "gpt-5.4";
        int hardTokenLimit = 1050000;

        // When
        ArtificialIntelligenceModel model = ArtificialIntelligenceModel.fromString(internalModelName);

        // Then
        assertThat(model).isNotNull()
                .hasToString(internalModelName);

        assertThat(model.getFullModelName()).isEqualTo(fullModelName);
        assertThat(model.toString()).isEqualTo(internalModelName);
        assertThat(model.getOfficialModelName()).isEqualTo(officialModelName);
        assertThat(model.getHardTokenLimit()).isEqualTo(hardTokenLimit);
    }

    @Test
    public void shouldReturnATenthOfTheHardLimitWhenReadingTheResponseTokenLimit() {

        // when / then
        assertThat(ArtificialIntelligenceModel.GPT54.getResponseTokenLimit()).isEqualTo(105000);
        assertThat(ArtificialIntelligenceModel.GPT54_MINI.getResponseTokenLimit()).isEqualTo(40000);
        assertThat(ArtificialIntelligenceModel.GPT54_NANO.getResponseTokenLimit()).isEqualTo(40000);
    }

    @Test
    public void shouldNeverExceedATenthOfTheHardLimitForAnyModel() {

        // when / then
        for (var model : ArtificialIntelligenceModel.values()) {
            assertThat(model.getResponseTokenLimit() * 10).isLessThanOrEqualTo(model.getHardTokenLimit());
        }
    }

    @Test
    public void errorWhenModelNotSupported() {

        // Given
        String modelName = "new_model_super_turbo";

        // Then
        assertThrows(UnsupportedAiModelException.class,
                () -> ArtificialIntelligenceModel.fromString(modelName));
    }
}
