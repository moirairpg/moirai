package me.moirai.storyengine.infrastructure.outbound.adapter.world;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.transaction.Transactional;
import me.moirai.storyengine.AbstractDatabaseIntegrationTest;
import me.moirai.storyengine.common.domain.Permission;
import me.moirai.storyengine.common.enums.PermissionLevel;
import me.moirai.storyengine.core.domain.userdetails.User;
import me.moirai.storyengine.core.domain.userdetails.UserFixture;
import me.moirai.storyengine.core.domain.world.World;
import me.moirai.storyengine.core.domain.world.WorldFixture;
import me.moirai.storyengine.core.port.outbound.world.WorldRepository;

@Transactional
public class WorldRepositoryImplIntegrationTest extends AbstractDatabaseIntegrationTest {

    @Autowired
    private WorldRepository repository;

    @BeforeEach
    public void before() {
        clearDatabase();
    }

    @Test
    public void shouldReturnOnlyWorldsWhereTheUserIsOwnerWhenFindingAllOwnedBy() {

        // given
        var owner = insertUser("11111", "owner");
        var reader = insertUser("22222", "reader");

        var owned = insertWorldWithPermissions(new Permission(owner.getId(), PermissionLevel.OWNER));
        insertWorldWithPermissions(new Permission(reader.getId(), PermissionLevel.READ));
        insertWorldWithPermissions(new Permission(reader.getId(), PermissionLevel.WRITE));

        // when
        var result = repository.findAllOwnedBy(owner.getId());

        // then
        assertThat(result)
                .extracting(World::getPublicId)
                .containsExactly(owned.getPublicId());
    }

    @Test
    public void shouldExcludeWorldsWhereTheUserOnlyReadsOrWritesWhenFindingAllOwnedBy() {

        // given
        var reader = insertUser("22222", "reader");

        insertWorldWithPermissions(new Permission(reader.getId(), PermissionLevel.READ));
        insertWorldWithPermissions(new Permission(reader.getId(), PermissionLevel.WRITE));

        // when
        var result = repository.findAllOwnedBy(reader.getId());

        // then
        assertThat(result).isEmpty();
    }

    @Test
    public void shouldReturnEveryWorldTheUserHoldsAPermissionOnWhenFindingAllInvolving() {

        // given
        var reader = insertUser("22222", "reader");

        var readable = insertWorldWithPermissions(new Permission(reader.getId(), PermissionLevel.READ));
        var writable = insertWorldWithPermissions(new Permission(reader.getId(), PermissionLevel.WRITE));

        // when
        var result = repository.findAllInvolving(reader.getId());

        // then
        assertThat(result)
                .extracting(World::getPublicId)
                .containsExactlyInAnyOrder(readable.getPublicId(), writable.getPublicId());
    }

    @Test
    public void shouldReturnTheWorldOnceWhenTheUserIsItsOwner() {

        // given
        var owner = insertUser("11111", "owner");
        var world = insertWorldWithPermissions(new Permission(owner.getId(), PermissionLevel.OWNER));

        // when
        var result = repository.findAllInvolving(owner.getId());

        // then
        assertThat(result)
                .extracting(World::getPublicId)
                .containsExactly(world.getPublicId());
    }

    @Test
    public void shouldReturnNothingWhenTheUserIsUnrelatedToAnyWorld() {

        // given
        var stranger = insertUser("77777", "stranger");
        var owner = insertUser("11111", "owner");

        insertWorldWithPermissions(new Permission(owner.getId(), PermissionLevel.OWNER));

        // when
        var result = repository.findAllInvolving(stranger.getId());

        // then
        assertThat(result).isEmpty();
    }

    private User insertUser(String discordId, String username) {

        return insert(UserFixture.player()
                .discordId(discordId)
                .username(username)
                .build(), User.class);
    }

    private World insertWorldWithPermissions(Permission... permissions) {

        return insert(WorldFixture.publicWorld()
                .permissions(permissions)
                .build(), World.class);
    }
}
