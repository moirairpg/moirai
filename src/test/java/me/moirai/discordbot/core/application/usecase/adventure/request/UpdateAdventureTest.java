package me.moirai.discordbot.core.application.usecase.adventure.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;

import org.assertj.core.util.Maps;
import org.junit.jupiter.api.Test;

import me.moirai.discordbot.core.domain.adventure.Adventure;
import me.moirai.discordbot.core.domain.adventure.AdventureFixture;

public class UpdateAdventureTest {

    @Test
    public void updateAdventure_whenValidDate_thenInstanceIsCreated() {

        // Given
        Adventure adventure = AdventureFixture.privateMultiplayerAdventure().build();
        UpdateAdventure.Builder builder = UpdateAdventure.builder()
                .id(adventure.getId())
                .name(adventure.getName())
                .description(adventure.getDescription())
                .worldId(adventure.getWorldId())
                .personaId(adventure.getPersonaId())
                .discordChannelId(adventure.getDiscordChannelId())
                .visibility(adventure.getVisibility().name())
                .aiModel(adventure.getModelConfiguration().getAiModel().getInternalModelName())
                .moderation(adventure.getModeration().name())
                .maxTokenLimit(adventure.getModelConfiguration().getMaxTokenLimit())
                .temperature(adventure.getModelConfiguration().getTemperature())
                .frequencyPenalty(adventure.getModelConfiguration().getFrequencyPenalty())
                .presencePenalty(adventure.getModelConfiguration().getPresencePenalty())
                .stopSequencesToAdd(adventure.getModelConfiguration().getStopSequences())
                .stopSequencesToRemove(adventure.getModelConfiguration().getStopSequences())
                .logitBiasToAdd(Maps.newHashMap("TKNID", 99D))
                .logitBiasToRemove(Collections.singletonList("TKN"))
                .usersAllowedToWriteToAdd(Collections.singletonList("USRID"))
                .usersAllowedToWriteToRemove(Collections.singletonList("USRID"))
                .usersAllowedToReadToAdd(Collections.singletonList("USRID"))
                .usersAllowedToReadToRemove(Collections.singletonList("USRID"))
                .gameMode(adventure.getGameMode().name())
                .requesterDiscordId(adventure.getOwnerDiscordId())
                .nudge(adventure.getContextAttributes().getNudge())
                .remember(adventure.getContextAttributes().getRemember())
                .authorsNote(adventure.getContextAttributes().getAuthorsNote())
                .bump(adventure.getContextAttributes().getBump())
                .bumpFrequency(adventure.getContextAttributes().getBumpFrequency())
                .isMultiplayer(adventure.isMultiplayer())
                .adventureStart(adventure.getAdventureStart())
                .maxTokenLimit(adventure.getModelConfiguration().getMaxTokenLimit());

        // When
        UpdateAdventure updateAdventure = builder.build();

        // Then
        assertThat(updateAdventure.getId()).isEqualTo(adventure.getId());
        assertThat(updateAdventure.getAdventureStart()).isEqualTo(adventure.getAdventureStart());
        assertThat(updateAdventure.getDescription()).isEqualTo(adventure.getDescription());
        assertThat(updateAdventure.getDiscordChannelId()).isEqualTo(adventure.getDiscordChannelId());
        assertThat(updateAdventure.getGameMode()).isEqualToIgnoringCase(adventure.getGameMode().name());
        assertThat(updateAdventure.getName()).isEqualTo(adventure.getName());
        assertThat(updateAdventure.getPersonaId()).isEqualTo(adventure.getPersonaId());
        assertThat(updateAdventure.getWorldId()).isEqualTo(adventure.getWorldId());
        assertThat(updateAdventure.getVisibility()).isEqualToIgnoringCase(adventure.getVisibility().name());
        assertThat(updateAdventure.getPresencePenalty())
                .isEqualTo(adventure.getModelConfiguration().getPresencePenalty());
        assertThat(updateAdventure.getFrequencyPenalty())
                .isEqualTo(adventure.getModelConfiguration().getFrequencyPenalty());
        assertThat(updateAdventure.getTemperature()).isEqualTo(adventure.getModelConfiguration().getTemperature());
        assertThat(updateAdventure.getMaxTokenLimit()).isEqualTo(adventure.getModelConfiguration().getMaxTokenLimit());
        assertThat(updateAdventure.getRemember()).isEqualTo(adventure.getContextAttributes().getRemember());
        assertThat(updateAdventure.getAuthorsNote()).isEqualTo(adventure.getContextAttributes().getAuthorsNote());
        assertThat(updateAdventure.getNudge()).isEqualTo(adventure.getContextAttributes().getNudge());
        assertThat(updateAdventure.getBump()).isEqualTo(adventure.getContextAttributes().getBump());
        assertThat(updateAdventure.getBumpFrequency()).isEqualTo(adventure.getContextAttributes().getBumpFrequency());

        assertThat(updateAdventure.getAiModel())
                .isEqualToIgnoringCase(adventure.getModelConfiguration().getAiModel().getInternalModelName());
    }

    @Test
    public void updateAdventure_whenStopSequencesToAddIsNull_thenListIsEmpty() {

        // Given
        UpdateAdventure.Builder builder = UpdateAdventureFixture.sample()
                .stopSequencesToAdd(null);

        // When
        UpdateAdventure updateAdventure = builder.build();

        // Then
        assertThat(updateAdventure.getStopSequencesToAdd()).isNotNull().isEmpty();
    }

    @Test
    public void updateAdventure_whenStopSequencesToRemoveIsNull_thenListIsEmpty() {

        // Given
        UpdateAdventure.Builder builder = UpdateAdventureFixture.sample()
                .stopSequencesToRemove(null);

        // When
        UpdateAdventure updateAdventure = builder.build();

        // Then
        assertThat(updateAdventure.getStopSequencesToRemove()).isNotNull().isEmpty();
    }

    @Test
    public void updateAdventure_whenLogitBiasToAddIsNull_thenListIsEmpty() {

        // Given
        UpdateAdventure.Builder builder = UpdateAdventureFixture.sample()
                .logitBiasToAdd(null);

        // When
        UpdateAdventure updateAdventure = builder.build();

        // Then
        assertThat(updateAdventure.getLogitBiasToAdd()).isNotNull().isEmpty();
    }

    @Test
    public void updateAdventure_whenLogitBiasToRemoveIsNull_thenListIsEmpty() {

        // Given
        UpdateAdventure.Builder builder = UpdateAdventureFixture.sample()
                .logitBiasToRemove(null);

        // When
        UpdateAdventure updateAdventure = builder.build();

        // Then
        assertThat(updateAdventure.getLogitBiasToRemove()).isNotNull().isEmpty();
    }

    @Test
    public void updateAdventure_whenUsersAllowedToWriteToAddIsNull_thenListIsEmpty() {

        // Given
        UpdateAdventure.Builder builder = UpdateAdventureFixture.sample()
                .usersAllowedToWriteToAdd(null);

        // When
        UpdateAdventure updateAdventure = builder.build();

        // Then
        assertThat(updateAdventure.getUsersAllowedToWriteToAdd()).isNotNull().isEmpty();
    }

    @Test
    public void updateAdventure_whenUsersAllowedToWriteToRemoveIsNull_thenListIsEmpty() {

        // Given
        UpdateAdventure.Builder builder = UpdateAdventureFixture.sample()
                .usersAllowedToWriteToRemove(null);

        // When
        UpdateAdventure updateAdventure = builder.build();

        // Then
        assertThat(updateAdventure.getUsersAllowedToWriteToRemove()).isNotNull().isEmpty();
    }

    @Test
    public void updateAdventure_whenUsersAllowedToReadToAddIsNull_thenListIsEmpty() {

        // Given
        UpdateAdventure.Builder builder = UpdateAdventureFixture.sample()
                .usersAllowedToReadToAdd(null);

        // When
        UpdateAdventure updateAdventure = builder.build();

        // Then
        assertThat(updateAdventure.getUsersAllowedToReadToAdd()).isNotNull().isEmpty();
    }

    @Test
    public void updateAdventure_whenUsersAllowedToReadToRemoveIsNull_thenListIsEmpty() {

        // Given
        UpdateAdventure.Builder builder = UpdateAdventureFixture.sample()
                .usersAllowedToReadToRemove(null);

        // When
        UpdateAdventure updateAdventure = builder.build();

        // Then
        assertThat(updateAdventure.getUsersAllowedToReadToRemove()).isNotNull().isEmpty();
    }
}
