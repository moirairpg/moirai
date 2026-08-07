package me.moirai.storyengine.infrastructure.outbound.adapter.message;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import me.moirai.storyengine.AbstractDatabaseIntegrationTest;
import me.moirai.storyengine.core.domain.adventure.Adventure;
import me.moirai.storyengine.core.domain.adventure.AdventureFixture;
import me.moirai.storyengine.core.domain.message.Message;
import me.moirai.storyengine.core.domain.message.MessageFixture;
import me.moirai.storyengine.core.domain.world.World;
import me.moirai.storyengine.core.domain.world.WorldFixture;
import me.moirai.storyengine.core.port.outbound.message.MessageAuthorizationReader;

public class MessageAuthorizationReaderImplIntegrationTest extends AbstractDatabaseIntegrationTest {

    @Autowired
    private MessageAuthorizationReader reader;

    @BeforeEach
    public void before() {
        clearDatabase();
    }

    @Test
    public void shouldReturnNothingWhenTheAdventureHasNoMessages() {

        // given
        var adventure = insertAdventure();

        // when
        var result = reader.getLastPlayerMessage(adventure.getPublicId());

        // then
        assertThat(result).isEmpty();
    }

    @Test
    public void shouldReturnTheAuthorIdOfTheLastPlayerMessage() {

        // given
        var adventure = insertAdventure();

        insert(MessageFixture.userMessage()
                .adventureId(adventure.getId())
                .authorId(1111L)
                .authorCharacterId(4L)
                .authorCharacterName("Aria")
                .build(), Message.class);

        // when
        var result = reader.getLastPlayerMessage(adventure.getPublicId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().authorId()).isEqualTo(1111L);
    }

    @Test
    public void shouldIgnoreNarratorMessagesWhenResolvingTheLastPlayerMessage() {

        // given
        var adventure = insertAdventure();

        var playerMessage = insert(MessageFixture.userMessage()
                .adventureId(adventure.getId())
                .authorId(1111L)
                .build(), Message.class);

        insert(MessageFixture.assistantMessage()
                .adventureId(adventure.getId())
                .build(), Message.class);

        // when
        var result = reader.getLastPlayerMessage(adventure.getPublicId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().messageId()).isEqualTo(playerMessage.getPublicId());
        assertThat(result.get().authorId()).isEqualTo(1111L);
    }

    @Test
    public void shouldReturnTheMostRecentPlayerMessageWhenSeveralExist() {

        // given
        var adventure = insertAdventure();

        insert(MessageFixture.userMessage()
                .adventureId(adventure.getId())
                .authorId(1111L)
                .build(), Message.class);

        var latest = insert(MessageFixture.userMessage()
                .adventureId(adventure.getId())
                .authorId(2222L)
                .build(), Message.class);

        // when
        var result = reader.getLastPlayerMessage(adventure.getPublicId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().messageId()).isEqualTo(latest.getPublicId());
        assertThat(result.get().authorId()).isEqualTo(2222L);
    }

    @Test
    public void shouldReturnNoAuthorWhenTheMessageWasNotBackfilled() {

        // given
        var adventure = insertAdventure();

        insert(MessageFixture.userMessage()
                .adventureId(adventure.getId())
                .build(), Message.class);

        // when
        var result = reader.getLastPlayerMessage(adventure.getPublicId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().authorId()).isNull();
    }

    private Adventure insertAdventure() {

        var world = insert(WorldFixture.publicWorld().build(), World.class);

        return insert(AdventureFixture.publicAdventure()
                .worldId(world.getPublicId())
                .build(), Adventure.class);
    }
}
