package me.moirai.storyengine.infrastructure.outbound.adapter.message;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import me.moirai.storyengine.AbstractDatabaseIntegrationTest;
import me.moirai.storyengine.core.domain.adventure.Adventure;
import me.moirai.storyengine.core.domain.adventure.AdventureFixture;
import me.moirai.storyengine.core.domain.message.Message;
import me.moirai.storyengine.core.domain.message.MessageFixture;
import me.moirai.storyengine.core.domain.userdetails.User;
import me.moirai.storyengine.core.domain.userdetails.UserFixture;
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
    public void shouldReturnTheAuthorPublicIdOfTheLastPlayerMessage() {

        // given
        var adventure = insertAdventure();
        var author = insertUser();

        insert(MessageFixture.userMessage()
                .adventureId(adventure.getId())
                .authorId(author.getId())
                .authorCharacterId(4L)
                .authorCharacterName("Aria")
                .build(), Message.class);

        // when
        var result = reader.getLastPlayerMessage(adventure.getPublicId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().authorId()).isEqualTo(author.getPublicId());
    }

    @Test
    public void shouldIgnoreNarratorMessagesWhenResolvingTheLastPlayerMessage() {

        // given
        var adventure = insertAdventure();
        var author = insertUser();

        var playerMessage = insert(MessageFixture.userMessage()
                .adventureId(adventure.getId())
                .authorId(author.getId())
                .build(), Message.class);

        insert(MessageFixture.assistantMessage()
                .adventureId(adventure.getId())
                .build(), Message.class);

        // when
        var result = reader.getLastPlayerMessage(adventure.getPublicId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().messageId()).isEqualTo(playerMessage.getPublicId());
        assertThat(result.get().authorId()).isEqualTo(author.getPublicId());
    }

    @Test
    public void shouldReturnTheMostRecentPlayerMessageWhenSeveralExist() {

        // given
        var adventure = insertAdventure();
        var author = insertUser();

        insert(MessageFixture.userMessage()
                .adventureId(adventure.getId())
                .authorId(author.getId())
                .build(), Message.class);

        var latest = insert(MessageFixture.userMessage()
                .adventureId(adventure.getId())
                .authorId(author.getId())
                .build(), Message.class);

        // when
        var result = reader.getLastPlayerMessage(adventure.getPublicId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().messageId()).isEqualTo(latest.getPublicId());
    }

    @Test
    public void shouldReturnTheLastPlayerMessageWhenItsAuthorWasNeverRecorded() {

        // given
        var adventure = insertAdventure();

        var message = insert(MessageFixture.userMessage()
                .adventureId(adventure.getId())
                .build(), Message.class);

        // when
        var result = reader.getLastPlayerMessage(adventure.getPublicId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().messageId()).isEqualTo(message.getPublicId());
        assertThat(result.get().authorId()).isNull();
    }

    @Test
    public void shouldReturnTheAuthorPublicIdOfTheMessage() {

        // given
        var adventure = insertAdventure();
        var author = insertUser();

        var message = insert(MessageFixture.userMessage()
                .adventureId(adventure.getId())
                .authorId(author.getId())
                .build(), Message.class);

        // when
        var result = reader.getMessageAuthor(message.getPublicId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().messageId()).isEqualTo(message.getPublicId());
        assertThat(result.get().authorId()).isEqualTo(author.getPublicId());
    }

    @Test
    public void shouldReturnNoAuthorWhenTheMessageIsANarratorMessage() {

        // given
        var adventure = insertAdventure();

        var message = insert(MessageFixture.assistantMessage()
                .adventureId(adventure.getId())
                .build(), Message.class);

        // when
        var result = reader.getMessageAuthor(message.getPublicId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().authorId()).isNull();
    }

    @Test
    public void shouldReturnNothingWhenTheMessageDoesNotExist() {

        // when
        var result = reader.getMessageAuthor(UUID.randomUUID());

        // then
        assertThat(result).isEmpty();
    }

    private User insertUser() {

        return insert(UserFixture.player().build(), User.class);
    }

    private Adventure insertAdventure() {

        var world = insert(WorldFixture.publicWorld().build(), World.class);

        return insert(AdventureFixture.publicAdventure()
                .worldId(world.getPublicId())
                .build(), Adventure.class);
    }
}
