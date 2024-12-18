package me.moirai.discordbot.core.application.usecase.adventure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.discordbot.common.exception.AssetNotFoundException;
import me.moirai.discordbot.core.application.port.AdventureQueryRepository;
import me.moirai.discordbot.core.application.usecase.adventure.request.GetAdventureByChannelId;
import me.moirai.discordbot.core.application.usecase.adventure.result.GetAdventureResult;
import me.moirai.discordbot.core.domain.adventure.Adventure;
import me.moirai.discordbot.core.domain.adventure.AdventureFixture;

@ExtendWith(MockitoExtension.class)
public class GetAdventureByChannelIdHandlerTest {

    @Mock
    private AdventureQueryRepository queryRepository;

    @InjectMocks
    private GetAdventureByChannelIdHandler handler;

    @Test
    public void getAdventure_whenAdventureNotFound_thenThrowException() {

        // Given
        String adventureId = "123123";
        GetAdventureByChannelId command = GetAdventureByChannelId.build(adventureId);

        when(queryRepository.findByDiscordChannelId(anyString())).thenReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> handler.execute(command))
                .isInstanceOf(AssetNotFoundException.class)
                .hasMessage("No adventures exist for this channel");
    }

    @Test
    public void getAdventure_whenAdventureIsFound_thenReturnResult() {

        // Given
        String adventureId = "123123";
        GetAdventureByChannelId command = GetAdventureByChannelId.build(adventureId);
        Adventure adventure = AdventureFixture.privateMultiplayerAdventure()
                .id(adventureId)
                .build();

        when(queryRepository.findByDiscordChannelId(anyString())).thenReturn(Optional.of(adventure));

        // When
        GetAdventureResult result = handler.execute(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(adventureId);
        assertThat(result.getId()).isEqualTo(adventure.getId());
        assertThat(result.getAdventureStart()).isEqualTo(adventure.getAdventureStart());
        assertThat(result.getDescription()).isEqualTo(adventure.getDescription());
        assertThat(result.getDiscordChannelId()).isEqualTo(adventure.getDiscordChannelId());
        assertThat(result.getGameMode()).isEqualTo(adventure.getGameMode().name());
        assertThat(result.getName()).isEqualTo(adventure.getName());
        assertThat(result.getOwnerDiscordId()).isEqualTo(adventure.getOwnerDiscordId());
        assertThat(result.getPersonaId()).isEqualTo(adventure.getPersonaId());
        assertThat(result.getVisibility()).isEqualTo(adventure.getVisibility().name());
        assertThat(result.getModeration()).isEqualTo(adventure.getModeration().name());
        assertThat(result.getWorldId()).isEqualTo(adventure.getWorldId());
        assertThat(result.isMultiplayer()).isEqualTo(adventure.isMultiplayer());
        assertThat(result.getCreationDate()).isNotNull();
        assertThat(result.getLastUpdateDate()).isNotNull();

        assertThat(result.getAuthorsNote()).isEqualTo(adventure.getContextAttributes().getAuthorsNote());
        assertThat(result.getNudge()).isEqualTo(adventure.getContextAttributes().getNudge());
        assertThat(result.getRemember()).isEqualTo(adventure.getContextAttributes().getRemember());
        assertThat(result.getBump()).isEqualTo(adventure.getContextAttributes().getBump());
        assertThat(result.getBumpFrequency()).isEqualTo(adventure.getContextAttributes().getBumpFrequency());

        assertThat(result.getAiModel())
                .isEqualToIgnoringCase(adventure.getModelConfiguration().getAiModel().getInternalModelName());
        assertThat(result.getFrequencyPenalty()).isEqualTo(adventure.getModelConfiguration().getFrequencyPenalty());
        assertThat(result.getLogitBias()).isEqualTo(adventure.getModelConfiguration().getLogitBias());
        assertThat(result.getMaxTokenLimit()).isEqualTo(adventure.getModelConfiguration().getMaxTokenLimit());
        assertThat(result.getPresencePenalty()).isEqualTo(adventure.getModelConfiguration().getPresencePenalty());
        assertThat(result.getStopSequences()).isEqualTo(adventure.getModelConfiguration().getStopSequences());
        assertThat(result.getTemperature()).isEqualTo(adventure.getModelConfiguration().getTemperature());

        assertThat(result.getUsersAllowedToRead()).hasSameElementsAs(adventure.getUsersAllowedToRead());
        assertThat(result.getUsersAllowedToWrite()).hasSameElementsAs(adventure.getUsersAllowedToWrite());
    }
}
