package me.moirai.storyengine.core.domain.world;

import static me.moirai.storyengine.common.enums.Visibility.PRIVATE;
import static me.moirai.storyengine.common.enums.Visibility.PUBLIC;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import me.moirai.storyengine.common.domain.Permission;
import me.moirai.storyengine.common.enums.PermissionLevel;
import me.moirai.storyengine.common.exception.BusinessRuleViolationException;

public class WorldTest {

    @Test
    public void makeWorldPublic() {

        // given
        var world = WorldFixture.privateWorld().build();

        // when
        world.updateVisibility(PUBLIC);

        // then
        assertThat(world.isPublic()).isTrue();
    }

    @Test
    public void makeWorldPrivate() {

        // given
        var world = WorldFixture.publicWorld().build();

        // when
        world.updateVisibility(PRIVATE);

        // then
        assertThat(world.isPublic()).isFalse();
    }

    @Test
    public void updateWorldName() {

        // given
        var world = WorldFixture.publicWorld().build();

        // when
        world.updateName("New Name");

        // then
        assertThat(world.getName()).isEqualTo("New Name");
    }

    @Test
    public void updateWorldDescription() {

        // given
        var world = WorldFixture.publicWorld().build();

        // when
        world.updateDescription("New Description");

        // then
        assertThat(world.getDescription()).isEqualTo("New Description");
    }

    @Test
    public void updateWorldInitialPrompt() {

        // given
        var world = WorldFixture.publicWorld().build();

        // when
        world.updateAdventureStart("New Prompt");

        // then
        assertThat(world.getAdventureStart()).isEqualTo("New Prompt");
    }

    @Test
    public void errorWhenCreatingWorldWithNullName() {

        // given
        var worldBuilder = WorldFixture.publicWorld().name(null);

        // then
        assertThrows(BusinessRuleViolationException.class, worldBuilder::build);
    }

    @Test
    public void errorWhenCreatingWorldWithEmptyName() {

        // given
        var worldBuilder = WorldFixture.publicWorld().name(EMPTY);

        // then
        assertThrows(BusinessRuleViolationException.class, worldBuilder::build);
    }

    @Test
    public void errorWhenCreatingWorldWithNullVisibility() {

        // given
        var worldBuilder = WorldFixture.publicWorld().visibility(null);

        // then
        assertThrows(BusinessRuleViolationException.class, worldBuilder::build);
    }

    @Test
    public void grantWritePermission_thenUserCanWrite() {

        // given
        var userId = 1234567890L;
        var world = WorldFixture.publicWorld().build();
        world.permissions().add(new Permission(9999L, PermissionLevel.OWNER));

        // when
        world.grant(new Permission(userId, PermissionLevel.WRITE));

        // then
        assertThat(world.canWrite(userId)).isTrue();
    }

    @Test
    public void grantReadPermission_thenUserCanRead() {

        // given
        var userId = 1234567890L;
        var world = WorldFixture.publicWorld().build();
        world.permissions().add(new Permission(9999L, PermissionLevel.OWNER));

        // when
        world.grant(new Permission(userId, PermissionLevel.READ));

        // then
        assertThat(world.canRead(userId)).isTrue();
    }

    @Test
    public void revokePermission_thenUserCannotRead() {

        // given
        var userId = 1234567890L;
        var world = WorldFixture.publicWorld().build();
        world.permissions().add(new Permission(9999L, PermissionLevel.OWNER));
        world.grant(new Permission(userId, PermissionLevel.READ));

        // when
        world.revoke(userId);

        // then
        assertThat(world.canRead(userId)).isFalse();
    }

    @Test
    public void revokePermission_thenUserCannotWrite() {

        // given
        var userId = 1234567890L;
        var world = WorldFixture.publicWorld().build();
        world.permissions().add(new Permission(9999L, PermissionLevel.OWNER));
        world.grant(new Permission(userId, PermissionLevel.WRITE));

        // when
        world.revoke(userId);

        // then
        assertThat(world.canWrite(userId)).isFalse();
    }

    @Test
    public void isOwner_whenUserIsOwner_thenReturnTrue() {

        // given
        var ownerId = 1234567890L;
        var world = WorldFixture.privateWorld().build();
        world.permissions().add(new Permission(ownerId, PermissionLevel.OWNER));

        // when
        var isOwner = world.isOwner(ownerId);

        // then
        assertThat(isOwner).isTrue();
    }

    @Test
    public void canWrite_whenUserIsOwner_thenReturnTrue() {

        // given
        var ownerId = 1234567890L;
        var world = WorldFixture.privateWorld().build();
        world.permissions().add(new Permission(ownerId, PermissionLevel.OWNER));

        // when
        var canWrite = world.canWrite(ownerId);

        // then
        assertThat(canWrite).isTrue();
    }

    @Test
    public void canRead_whenUserIsOwner_thenReturnTrue() {

        // given
        var ownerId = 1234567890L;
        var world = WorldFixture.privateWorld().build();
        world.permissions().add(new Permission(ownerId, PermissionLevel.OWNER));

        // when
        var canRead = world.canRead(ownerId);

        // then
        assertThat(canRead).isTrue();
    }

    @Test
    public void canWrite_whenUserHasNoPermission_thenReturnFalse() {

        // given
        var userId = 1234567890L;
        var world = WorldFixture.privateWorld().build();

        // when
        var canWrite = world.canWrite(userId);

        // then
        assertThat(canWrite).isFalse();
    }

    @Test
    public void canRead_whenUserHasNoPermission_thenReturnFalse() {

        // given
        var userId = 1234567890L;
        var world = WorldFixture.privateWorld().build();

        // when
        var canRead = world.canRead(userId);

        // then
        assertThat(canRead).isFalse();
    }

    @Test
    public void createWorld_whenNarratorProvided_thenExposeNameAndPersonality() {

        // given
        var world = WorldFixture.publicWorld()
                .narrator("Aria", "A helpful guide")
                .build();

        // when / then
        assertThat(world.getNarratorName()).isEqualTo("Aria");
        assertThat(world.getNarratorPersonality()).isEqualTo("A helpful guide");
    }

    @Test
    public void createWorld_whenNarratorPersonalitySetButNameNull_thenNameDefaultsToNarrator() {

        // given
        var world = WorldFixture.publicWorld()
                .narrator(null, "Some personality")
                .build();

        // when / then
        assertThat(world.getNarratorName()).isEqualTo("Narrator");
        assertThat(world.getNarratorPersonality()).isEqualTo("Some personality");
    }

    @Test
    public void shouldRaiseADeletionEventCarryingItsIdentityWhenTheDeletionIsCommunicated() {

        // given
        var world = WorldFixture.privateWorldWithId();
        ReflectionTestUtils.setField(world, "imageKey", "worlds/keep.png");

        // when
        world.communicateWorldDeleted();

        // then
        var events = world.drainEvents();

        assertThat(events).singleElement().isInstanceOf(WorldDeletedEvent.class);

        var event = (WorldDeletedEvent) events.getFirst();

        assertThat(event.getPublicId()).isEqualTo(WorldFixture.PUBLIC_ID);
        assertThat(event.getImageKey()).isEqualTo("worlds/keep.png");
    }

    @Test
    public void shouldEmptyTheEventListWhenEventsAreDrained() {

        // given
        var world = WorldFixture.privateWorldWithId();
        world.communicateWorldDeleted();

        // when
        world.drainEvents();

        // then
        assertThat(world.drainEvents()).isEmpty();
    }
}
