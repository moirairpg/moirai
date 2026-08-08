package me.moirai.storyengine.infrastructure.outbound.adapter.adventure;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import me.moirai.storyengine.AbstractDatabaseIntegrationTest;
import me.moirai.storyengine.common.domain.Permission;
import me.moirai.storyengine.common.dto.PermissionDto;
import me.moirai.storyengine.common.enums.PermissionLevel;
import me.moirai.storyengine.core.domain.adventure.Adventure;
import me.moirai.storyengine.core.domain.adventure.AdventureFixture;
import me.moirai.storyengine.core.domain.character.PlayerCharacter;
import me.moirai.storyengine.core.domain.character.PlayerCharacterFixture;
import me.moirai.storyengine.core.domain.userdetails.User;
import me.moirai.storyengine.core.domain.userdetails.UserFixture;
import me.moirai.storyengine.core.domain.world.World;
import me.moirai.storyengine.core.domain.world.WorldFixture;
import me.moirai.storyengine.core.port.outbound.adventure.AdventureDetailsRow;
import me.moirai.storyengine.core.port.outbound.adventure.AdventureReader;

public class AdventureReaderImplIntegrationTest extends AbstractDatabaseIntegrationTest {

    @Autowired
    private AdventureReader reader;

    @BeforeEach
    public void before() {
        clearDatabase();
    }

    @Test
    public void getAdventureById_whenNotFound_thenReturnEmpty() {

        // Given
        var publicId = UUID.randomUUID();

        // When
        var result = reader.getAdventureById(publicId);

        // Then
        assertThat(result).isNotNull().isEmpty();
    }

    @Test
    public void getAdventureById_whenFound_thenReturnDetails() {

        // Given
        var world = insert(WorldFixture.publicWorld().build(), World.class);
        var adventure = AdventureFixture.privateAdventure()
                .worldId(world.getPublicId())
                .build();

        insert(adventure, Adventure.class);

        // When
        Optional<AdventureDetailsRow> result = reader.getAdventureById(adventure.getPublicId());

        // Then
        assertThat(result).isNotNull().isNotEmpty();
        assertThat(result.get().id()).isEqualTo(adventure.getPublicId());
        assertThat(result.get().name()).isEqualTo(adventure.getName());
        assertThat(result.get().worldId()).isEqualTo(world.getPublicId());
        assertThat(result.get().narratorName()).isEqualTo(adventure.getNarratorName());
        assertThat(result.get().narratorPersonality()).isEqualTo(adventure.getNarratorPersonality());
        assertThat(result.get().visibility()).isEqualTo(adventure.getVisibility());
        assertThat(result.get().creationDate()).isNotNull();
        assertThat(result.get().lastUpdateDate()).isNotNull();

        assertThat(result.get().modelConfiguration()).isNotNull();
        assertThat(result.get().modelConfiguration().maxTokenLimit()).isEqualTo(100);
        assertThat(result.get().modelConfiguration().temperature()).isEqualTo(1.0);

        assertThat(result.get().contextAttributes()).isNotNull();
        assertThat(result.get().contextAttributes().nudge()).isEqualTo("Nudge");
        assertThat(result.get().contextAttributes().authorsNote()).isEqualTo("Author's note");
        assertThat(result.get().contextAttributes().scene()).isEqualTo("Scene");
        assertThat(result.get().contextAttributes().bump()).isEqualTo("Bump");
        assertThat(result.get().contextAttributes().bumpFrequency()).isEqualTo(1);
        assertThat(result.get().uiImagePositionX()).isNull();
        assertThat(result.get().uiImagePositionY()).isNull();
    }

    @Test
    public void getAdventureById_returnsPermissionsKeyedByUserPublicId() {

        // Given
        var user = insert(UserFixture.player().build(), User.class);
        var world = insert(WorldFixture.publicWorld().build(), World.class);
        var adventure = AdventureFixture.privateAdventure()
                .worldId(world.getPublicId())
                .permissions(new Permission(user.getId(), PermissionLevel.OWNER))
                .build();

        insert(adventure, Adventure.class);

        // When
        var result = reader.getAdventureById(adventure.getPublicId());

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.get().permissions())
                .extracting(PermissionDto::userId)
                .containsExactly(user.getPublicId());
        assertThat(result.get().permissions())
                .noneMatch(permission -> permission.userId().equals(adventure.getPublicId()));
    }

    @Test
    public void getAdventureById_whenFocalPointSet_thenReturnFocalPoint() {

        // Given
        var world = insert(WorldFixture.publicWorld().build(), World.class);
        var adventure = AdventureFixture.privateAdventure()
                .worldId(world.getPublicId())
                .build();

        var saved = insert(adventure, Adventure.class);
        saved.updateUiImagePosition(0.3, 0.7);
        update(saved, saved.getId(), Adventure.class);

        // When
        var result = reader.getAdventureById(saved.getPublicId());

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.get().uiImagePositionX()).isEqualTo(0.3);
        assertThat(result.get().uiImagePositionY()).isEqualTo(0.7);
    }

    @Test
    public void getEnrolledPlayerIds_whenPlayersEnrolled_thenReturnTheirPublicIds() {

        // Given
        var world = insert(WorldFixture.publicWorld().build(), World.class);
        var owner = insert(UserFixture.player().build(), User.class);
        var otherOwner = insert(UserFixture.player().discordId("55555").username("player.two").build(), User.class);

        var firstCharacter = insertCharacter(owner, "Volin Habar");
        var secondCharacter = insertCharacter(otherOwner, "Mira");

        var adventure = insert(AdventureFixture.privateAdventure()
                .worldId(world.getPublicId())
                .build(), Adventure.class);

        adventure.enrollPlayerCharacter(firstCharacter.getId(), firstCharacter.getPlayerId());
        adventure.enrollPlayerCharacter(secondCharacter.getId(), secondCharacter.getPlayerId());
        update(adventure, adventure.getId(), Adventure.class);

        // When
        List<UUID> result = reader.getEnrolledPlayerIds(adventure.getPublicId());

        // Then
        assertThat(result).containsExactlyInAnyOrder(owner.getPublicId(), otherOwner.getPublicId());
    }

    @Test
    public void getEnrolledPlayerIds_whenNobodyEnrolled_thenReturnEmpty() {

        // Given
        var world = insert(WorldFixture.publicWorld().build(), World.class);
        var adventure = insert(AdventureFixture.privateAdventure()
                .worldId(world.getPublicId())
                .build(), Adventure.class);

        // When
        List<UUID> result = reader.getEnrolledPlayerIds(adventure.getPublicId());

        // Then
        assertThat(result).isNotNull().isEmpty();
    }

    @Test
    public void getEnrolledPlayerIds_whenAdventureDoesNotExist_thenReturnEmpty() {

        // Given
        var publicId = UUID.randomUUID();

        // When
        List<UUID> result = reader.getEnrolledPlayerIds(publicId);

        // Then
        assertThat(result).isNotNull().isEmpty();
    }

    private PlayerCharacter insertCharacter(User owner, String name) {

        return insert(PlayerCharacterFixture.samplePlayerCharacter()
                .name(name)
                .playerId(owner.getId())
                .build(), PlayerCharacter.class);
    }
}
