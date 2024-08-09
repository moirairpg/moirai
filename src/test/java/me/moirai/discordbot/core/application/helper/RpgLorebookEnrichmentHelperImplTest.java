package me.moirai.discordbot.core.application.helper;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.discordbot.core.application.usecase.discord.DiscordMessageData;
import me.moirai.discordbot.core.application.usecase.discord.DiscordUserDetails;
import me.moirai.discordbot.core.domain.port.TokenizerPort;
import me.moirai.discordbot.core.domain.world.WorldLorebookEntry;
import me.moirai.discordbot.core.domain.world.WorldService;
import me.moirai.discordbot.infrastructure.outbound.adapter.request.ModelConfigurationRequest;
import me.moirai.discordbot.infrastructure.outbound.adapter.request.ModelConfigurationRequestFixture;

@ExtendWith(MockitoExtension.class)
public class RpgLorebookEnrichmentHelperImplTest {

    @Mock
    private TokenizerPort tokenizerPort;

    @Mock
    private WorldService worldService;

    @Mock
    private ChatMessageHelper chatMessageService;

    @InjectMocks
    private RpgLorebookEnrichmentHelperImpl adapter;

    @Test
    void enrichContextWithLorebook_whenMessagesAreValid_thenReturnContextWithProcessedPlayerEntries() {

        // Given
        String worldId = "WRLDID";
        ModelConfigurationRequest modelConfiguration = ModelConfigurationRequestFixture.gpt4Mini().build();
        List<DiscordMessageData> messageList = getMessageListForTesting();

        stubLorebookEntriesByWords();
        stubLorebookEntriesByMention();
        stubLorebookEntriesByAuthor();

        when(tokenizerPort.getTokenCountFrom(anyString()))
                .thenReturn(5)
                .thenReturn(10)
                .thenReturn(5)
                .thenReturn(10)
                .thenReturn(5)
                .thenReturn(10)
                .thenReturn(5)
                .thenReturn(10)
                .thenReturn(5)
                .thenReturn(10);

        // When
        adapter.enrichContextWithLorebook(messageList, worldId, modelConfiguration);

        // Then
    }

    private List<DiscordMessageData> getMessageListForTesting() {

        DiscordUserDetails marcus = DiscordUserDetails.builder()
                .id("1")
                .mention("<@1>")
                .nickname("Little Marcus")
                .username("Marcus")
                .build();

        DiscordUserDetails john = DiscordUserDetails.builder()
                .id("2")
                .mention("<@2>")
                .nickname("JoeJoe")
                .username("John")
                .build();

        DiscordMessageData firstMessage = DiscordMessageData.builder()
                .id("1")
                .content("Little Marcus says: I pull the Sword of Fire and charge against the Lord of Doom.")
                .author(marcus)
                .build();

        DiscordMessageData secondMessage = DiscordMessageData.builder()
                .id("2")
                .content("JoeJoe says: I deflect Little Marcus's attack and attack back with my Glove of Armageddon.")
                .author(john)
                .mentionedUsers(Lists.list(marcus))
                .build();

        DiscordMessageData thirdMessage = DiscordMessageData.builder()
                .id("3")
                .content("Little Marcus says: I cast a ball of fire and deal fire damage.")
                .author(marcus)
                .build();

        return Lists.list(firstMessage, secondMessage, thirdMessage);
    }

    private void stubLorebookEntriesByAuthor() {

        WorldLorebookEntry marcusCharacter = WorldLorebookEntry.builder()
                .id("1")
                .name("Pyromancer")
                .regex("[Pp]iro[Mm]ancer")
                .description("The Pyromancer is a fire battlemage")
                .playerDiscordId("1")
                .isPlayerCharacter(true)
                .worldId("WRLDID")
                .build();

        WorldLorebookEntry johnCharacter = WorldLorebookEntry.builder()
                .id("2")
                .name("Lord of Doom")
                .regex("[Ll]ord [Oo] [Dd]oom")
                .description("The Lord of Doom is a very powerful ogre")
                .playerDiscordId("2")
                .isPlayerCharacter(true)
                .worldId("WRLDID")
                .build();

        when(worldService.findLorebookEntryByPlayerDiscordId(anyString(), anyString()))
                .thenReturn(marcusCharacter)
                .thenReturn(johnCharacter)
                .thenReturn(marcusCharacter);
    }

    private void stubLorebookEntriesByMention() {

        WorldLorebookEntry marcusCharacter = WorldLorebookEntry.builder()
                .id("1")
                .name("Pyromancer")
                .regex("[Pp]iro[Mm]ancer")
                .description("The Pyromancer is a fire battlemage")
                .playerDiscordId("1")
                .isPlayerCharacter(true)
                .worldId("WRLDID")
                .build();

        when(worldService.findLorebookEntryByPlayerDiscordId(anyString(), anyString()))
                .thenReturn(marcusCharacter);

    }

    private void stubLorebookEntriesByWords() {

        WorldLorebookEntry swordOfFire = WorldLorebookEntry.builder()
                .id("3")
                .name("Sword of Fire")
                .regex("[Ss]word [Oo]f [Ff]ire")
                .description("The Sword of Fire is a spectral sword that spits fire")
                .worldId("WRLDID")
                .build();

        WorldLorebookEntry gloveOfArmageddon = WorldLorebookEntry.builder()
                .id("4")
                .name("Glove of Armageddon")
                .regex("[Gg]love [Oo]f [Aa]rmageddon")
                .description("The Glove of Armageddon is a gauntlet that punches with the strength of three suns")
                .worldId("WRLDID")
                .build();

        WorldLorebookEntry lordOfDoom = WorldLorebookEntry.builder()
                .id("2")
                .name("Lord of Doom")
                .regex("[Ll]ord [Oo] [Dd]oom")
                .description("The Lord of Doom is a very powerful ogre")
                .playerDiscordId("2")
                .isPlayerCharacter(true)
                .worldId("WRLDID")
                .build();

        when(worldService.findAllLorebookEntriesByRegex(anyString(), anyString()))
                .thenReturn(Lists.list(swordOfFire, gloveOfArmageddon, lordOfDoom));
    }
}
