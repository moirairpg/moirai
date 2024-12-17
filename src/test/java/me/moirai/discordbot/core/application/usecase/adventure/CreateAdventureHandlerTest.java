package me.moirai.discordbot.core.application.usecase.adventure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.list;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.discordbot.common.exception.AssetNotFoundException;
import me.moirai.discordbot.core.application.port.WorldQueryRepository;
import me.moirai.discordbot.core.application.usecase.adventure.request.CreateAdventure;
import me.moirai.discordbot.core.application.usecase.adventure.request.CreateAdventureFixture;
import me.moirai.discordbot.core.application.usecase.adventure.result.CreateAdventureResult;
import me.moirai.discordbot.core.domain.adventure.Adventure;
import me.moirai.discordbot.core.domain.adventure.AdventureDomainRepository;
import me.moirai.discordbot.core.domain.adventure.AdventureFixture;
import me.moirai.discordbot.core.domain.adventure.AdventureLorebookEntryRepository;
import me.moirai.discordbot.core.domain.world.World;
import me.moirai.discordbot.core.domain.world.WorldFixture;
import me.moirai.discordbot.core.domain.world.WorldLorebookEntryFixture;
import me.moirai.discordbot.core.domain.world.WorldLorebookEntryRepository;

@ExtendWith(MockitoExtension.class)
public class CreateAdventureHandlerTest {

    @Mock
    private WorldLorebookEntryRepository worldLorebookEntryRepository;

    @Mock
    private WorldQueryRepository worldQueryRepository;

    @Mock
    private AdventureDomainRepository repository;

    @Mock
    private AdventureLorebookEntryRepository lorebookEntryRepository;

    @InjectMocks
    private CreateAdventureHandler handler;

    @Test
    public void createAdventure_whenWorldNotFound_thenThrowException() {

        // Given
        CreateAdventure command = CreateAdventureFixture.sample().build();

        when(worldQueryRepository.findById(anyString())).thenReturn(Optional.empty());

        // Then
        assertThrows(AssetNotFoundException.class, () -> handler.handle(command));
    }

    @Test
    public void createAdventure_whenValidDate_thenAdventureIsCreated() {

        // Given
        String id = "HAUDHUAHD";
        Adventure adventure = AdventureFixture.privateMultiplayerAdventure().id(id).build();
        World world = WorldFixture.privateWorld().build();

        CreateAdventure command = CreateAdventureFixture.sample().build();

        when(worldQueryRepository.findById(anyString())).thenReturn(Optional.of(world));
        when(repository.save(any(Adventure.class))).thenReturn(adventure);
        when(worldLorebookEntryRepository.findAllByWorldId(anyString()))
                .thenReturn(list(WorldLorebookEntryFixture.sampleLorebookEntry().build()));

        // When
        CreateAdventureResult result = handler.handle(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
    }
}
