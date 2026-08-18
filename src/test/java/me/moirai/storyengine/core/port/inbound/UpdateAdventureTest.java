package me.moirai.storyengine.core.port.inbound;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import me.moirai.storyengine.core.domain.adventure.Adventure;
import me.moirai.storyengine.core.domain.adventure.AdventureFixture;
import me.moirai.storyengine.core.domain.userdetails.UserFixture;
import me.moirai.storyengine.core.port.inbound.adventure.ContextAttributesDto;
import me.moirai.storyengine.core.port.inbound.adventure.ModelConfigurationDto;
import me.moirai.storyengine.core.port.inbound.adventure.UpdateAdventure;

public class UpdateAdventureTest {

    @Test
    public void updateAdventure_whenValidDate_thenInstanceIsCreated() {

        // given
        Adventure adventure = AdventureFixture.privateAdventure().build();

        // when
        var updateAdventure = new UpdateAdventure(
                AdventureFixture.PUBLIC_ID,
                adventure.getName(),
                adventure.getDescription(),
                adventure.getAdventureStart(),
                adventure.getNarratorName(),
                adventure.getNarratorPersonality(),
                adventure.getModeration(),
                true,
                null,
                null,
                new ModelConfigurationDto(
                        adventure.getModelConfiguration().getAiModel(),
                        adventure.getModelConfiguration().getMaxTokenLimit(),
                        adventure.getModelConfiguration().getTemperature()),
                new ContextAttributesDto(
                        adventure.getContextAttributes().nudge(),
                        adventure.getContextAttributes().authorsNote(),
                        adventure.getContextAttributes().scene(),
                        adventure.getContextAttributes().bump(),
                        adventure.getContextAttributes().bumpFrequency()),
                List.of(),
                List.of(),
                List.of(),
                UserFixture.PUBLIC_ID);

        // then
        assertThat(updateAdventure.adventureId()).isEqualTo(AdventureFixture.PUBLIC_ID);
        assertThat(updateAdventure.adventureStart()).isEqualTo(adventure.getAdventureStart());
        assertThat(updateAdventure.description()).isEqualTo(adventure.getDescription());
        assertThat(updateAdventure.name()).isEqualTo(adventure.getName());
        assertThat(updateAdventure.narratorName()).isEqualTo(adventure.getNarratorName());
        assertThat(updateAdventure.modelConfiguration().temperature()).isEqualTo(adventure.getModelConfiguration().getTemperature());
        assertThat(updateAdventure.modelConfiguration().maxTokenLimit()).isEqualTo(adventure.getModelConfiguration().getMaxTokenLimit());
        assertThat(updateAdventure.contextAttributes().scene()).isEqualTo(adventure.getContextAttributes().scene());
        assertThat(updateAdventure.contextAttributes().authorsNote()).isEqualTo(adventure.getContextAttributes().authorsNote());
        assertThat(updateAdventure.contextAttributes().nudge()).isEqualTo(adventure.getContextAttributes().nudge());
        assertThat(updateAdventure.contextAttributes().bump()).isEqualTo(adventure.getContextAttributes().bump());
        assertThat(updateAdventure.contextAttributes().bumpFrequency()).isEqualTo(adventure.getContextAttributes().bumpFrequency());
        assertThat(updateAdventure.modelConfiguration().aiModel()).isEqualTo(adventure.getModelConfiguration().getAiModel());
    }

    @Test
    public void updateAdventure_whenLorebookListsAreNull_thenListsAreEmpty() {

        // given
        var sample = UpdateAdventureFixture.sample();

        // when
        var updateAdventure = new UpdateAdventure(
                sample.adventureId(),
                sample.name(),
                sample.description(),
                sample.adventureStart(),
                sample.narratorName(),
                sample.narratorPersonality(),
                sample.moderation(),
                true,
                null,
                null,
                sample.modelConfiguration(),
                sample.contextAttributes(),
                null,
                null,
                null,
                UserFixture.PUBLIC_ID);

        // then
        assertThat(updateAdventure.lorebookEntriesToAdd()).isEmpty();
        assertThat(updateAdventure.lorebookEntriesToUpdate()).isEmpty();
        assertThat(updateAdventure.lorebookEntriesToDelete()).isEmpty();
    }
}
