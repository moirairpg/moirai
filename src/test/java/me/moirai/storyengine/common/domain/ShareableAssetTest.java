package me.moirai.storyengine.common.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.Set;

import org.junit.jupiter.api.Test;

import me.moirai.storyengine.common.enums.PermissionLevel;
import me.moirai.storyengine.common.exception.BusinessRuleViolationException;
import me.moirai.storyengine.core.domain.world.WorldFixture;

public class ShareableAssetTest {

    private static final Long READER_ID = 2222L;
    private static final Long WRITER_ID = 3333L;

    @Test
    void shouldThrowExceptionWhenAnEntryNamesTheOwner() {

        // given
        var world = WorldFixture.privateWorld().build();
        var newPermissions = Set.of(new Permission(WorldFixture.OWNER_ID, PermissionLevel.WRITE));

        // when
        var thrown = assertThatExceptionOfType(BusinessRuleViolationException.class)
                .isThrownBy(() -> world.updatePermissions(newPermissions));

        // then
        thrown.withMessage("Owner permission cannot be overwritten");
        assertThat(world.getPermissions()).hasSize(1);
        assertThat(world.isOwner(WorldFixture.OWNER_ID)).isTrue();
    }

    @Test
    void shouldThrowExceptionWhenAnEntryNamesTheOwnerAtReadLevel() {

        // given
        var world = WorldFixture.privateWorld().build();
        var newPermissions = Set.of(new Permission(WorldFixture.OWNER_ID, PermissionLevel.READ));

        // when
        var thrown = assertThatExceptionOfType(BusinessRuleViolationException.class)
                .isThrownBy(() -> world.updatePermissions(newPermissions));

        // then
        thrown.withMessage("Owner permission cannot be overwritten");
        assertThat(world.getPermissions()).hasSize(1);
    }

    @Test
    void shouldDiscardAnOwnerLevelEntryForAnotherUser() {

        // given
        var world = WorldFixture.privateWorld().build();
        var newPermissions = Set.of(new Permission(WRITER_ID, PermissionLevel.OWNER));

        // when
        world.updatePermissions(newPermissions);

        // then
        assertThat(world.getPermissions()).hasSize(1);
        assertThat(world.isOwner(WorldFixture.OWNER_ID)).isTrue();
        assertThat(world.isOwner(WRITER_ID)).isFalse();
    }

    @Test
    void shouldReplaceEveryNonOwnerPermission() {

        // given
        var world = WorldFixture.privateWorld().build();
        world.updatePermissions(Set.of(new Permission(READER_ID, PermissionLevel.READ)));

        // when
        world.updatePermissions(Set.of(new Permission(WRITER_ID, PermissionLevel.WRITE)));

        // then
        assertThat(world.getPermissions()).hasSize(2);
        assertThat(world.canRead(READER_ID)).isFalse();
        assertThat(world.canWrite(WRITER_ID)).isTrue();
        assertThat(world.isOwner(WorldFixture.OWNER_ID)).isTrue();
    }

    @Test
    void shouldLeaveOnlyTheOwnerWhenTheListIsEmpty() {

        // given
        var world = WorldFixture.privateWorld().build();
        world.updatePermissions(Set.of(new Permission(READER_ID, PermissionLevel.READ)));

        // when
        world.updatePermissions(Set.of());

        // then
        assertThat(world.getPermissions()).hasSize(1);
        assertThat(world.isOwner(WorldFixture.OWNER_ID)).isTrue();
    }

    @Test
    void shouldKeepTheWeakestLevelWhenAUserAppearsTwice() {

        // given
        var world = WorldFixture.privateWorld().build();
        var newPermissions = Set.of(
                new Permission(READER_ID, PermissionLevel.READ),
                new Permission(READER_ID, PermissionLevel.WRITE));

        // when
        world.updatePermissions(newPermissions);

        // then
        assertThat(world.getPermissions()).hasSize(2);
        assertThat(world.canRead(READER_ID)).isTrue();
        assertThat(world.canWrite(READER_ID)).isFalse();
    }
}
