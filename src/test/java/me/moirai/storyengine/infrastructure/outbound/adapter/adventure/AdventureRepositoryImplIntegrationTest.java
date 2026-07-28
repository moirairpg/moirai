package me.moirai.storyengine.infrastructure.outbound.adapter.adventure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import me.moirai.storyengine.AbstractDatabaseIntegrationTest;
import me.moirai.storyengine.common.domain.Permission;
import me.moirai.storyengine.common.enums.PermissionLevel;
import me.moirai.storyengine.core.domain.adventure.Adventure;
import me.moirai.storyengine.core.domain.adventure.AdventureFixture;
import me.moirai.storyengine.core.domain.character.PlayerCharacter;
import me.moirai.storyengine.core.domain.character.PlayerCharacterFixture;
import me.moirai.storyengine.core.domain.userdetails.User;
import me.moirai.storyengine.core.domain.userdetails.UserFixture;
import me.moirai.storyengine.core.domain.world.World;
import me.moirai.storyengine.core.domain.world.WorldFixture;
import me.moirai.storyengine.core.port.outbound.adventure.AdventureRepository;

@Transactional
public class AdventureRepositoryImplIntegrationTest extends AbstractDatabaseIntegrationTest {

    @Autowired
    private AdventureRepository repository;

    @Autowired
    private JdbcClient jdbcClient;

    @PersistenceContext
    private EntityManager entityManager;

    private World world;

    @BeforeEach
    public void before() {

        clearDatabase();
        world = insert(WorldFixture.publicWorld().build(), World.class);
    }

    @Test
    public void shouldFindEveryAdventureContainingTheCharacter() {

        // given
        var owner = insertUser("11111", "player.one");
        var otherOwner = insertUser("44444", "player.two");
        var enrolledCharacter = insertCharacter(owner, "Enrolled");
        var otherCharacter = insertCharacter(otherOwner, "Other");

        var firstAdventure = insertAdventureWithRoster(enrolledCharacter, otherCharacter);
        var secondAdventure = insertAdventureWithRoster(enrolledCharacter);
        insertAdventureWithRoster(otherCharacter);

        // when
        var result = repository.findAllContainingCharacter(enrolledCharacter.getId());

        // then
        assertThat(result)
                .extracting(Adventure::getId)
                .containsExactlyInAnyOrder(firstAdventure.getId(), secondAdventure.getId());
    }

    @Test
    public void shouldReturnOwnerAndWriterIdsAsManagersAndExcludeReaders() {

        // given
        var writer = insertUser("22222", "adventure.writer");
        var reader = insertUser("33333", "adventure.reader");

        var adventure = insertAdventureWithPermissions(
                new Permission(writer.getId(), PermissionLevel.WRITE),
                new Permission(reader.getId(), PermissionLevel.READ));

        // when
        var result = repository.findManagerUserIdsByAdventureId(adventure.getId());

        // then
        assertThat(result).containsExactlyInAnyOrder(AdventureFixture.OWNER_ID, writer.getId());
        assertThat(result).doesNotContain(reader.getId());
    }

    @Test
    public void shouldNotReturnManagersFromOtherAdventures() {

        // given
        var writer = insertUser("22222", "adventure.writer");

        insertAdventureWithPermissions(new Permission(writer.getId(), PermissionLevel.WRITE));
        var otherAdventure = insertAdventureWithPermissions();

        // when
        var result = repository.findManagerUserIdsByAdventureId(otherAdventure.getId());

        // then
        assertThat(result).doesNotContain(writer.getId());
    }

    @Test
    public void shouldReturnEnrolledCharacterNameForUsername() {

        // given
        var owner = insertUser("11111", "player.one");
        var character = insertCharacter(owner, "Volin Habar");
        var adventure = insertAdventureWithRoster(character);

        // when
        var result = repository.findEnrolledCharacterName(adventure.getId(), owner.getUsername());

        // then
        assertThat(result).contains("Volin Habar");
    }

    @Test
    public void shouldReturnEmptyNameWhenPlayerHasNoCharacterEnrolled() {

        // given
        var owner = insertUser("11111", "player.one");
        var adventure = insertAdventureWithRoster();

        // when
        var result = repository.findEnrolledCharacterName(adventure.getId(), owner.getUsername());

        // then
        assertThat(result).isEmpty();
    }

    @Test
    public void shouldReturnEmptyNameWhenCharacterIsEnrolledInAnotherAdventure() {

        // given
        var owner = insertUser("11111", "player.one");
        var character = insertCharacter(owner, "Volin Habar");

        insertAdventureWithRoster(character);
        var otherAdventure = insertAdventureWithRoster();

        // when
        var result = repository.findEnrolledCharacterName(otherAdventure.getId(), owner.getUsername());

        // then
        assertThat(result).isEmpty();
    }

    @Test
    public void shouldReturnEmptyNameWhenUsernameIsUnknown() {

        // given
        var owner = insertUser("11111", "player.one");
        var character = insertCharacter(owner, "Volin Habar");
        var adventure = insertAdventureWithRoster(character);

        // when
        var result = repository.findEnrolledCharacterName(adventure.getId(), "does.not.exist");

        // then
        assertThat(result).isEmpty();
    }

    @Test
    public void shouldRemoveRosterRowsWhenAdventureIsDeleted() {

        // given
        var owner = insertUser("11111", "player.one");
        var character = insertCharacter(owner, "Enrolled");

        var adventureToDelete = insertAdventureWithRoster(character);
        var adventureToKeep = insertAdventureWithRoster(character);

        // when
        repository.deleteByPublicId(adventureToDelete.getPublicId());
        entityManager.flush();

        // then
        assertThat(rosterOf(adventureToDelete)).isEmpty();
        assertThat(rosterOf(adventureToKeep)).containsExactly(character.getId());
    }

    @Test
    public void shouldRemoveChronicleSegmentsWhenAdventureIsDeleted() {

        // given
        var adventureToDelete = insertAdventureWithRoster();
        var adventureToKeep = insertAdventureWithRoster();

        adventureToDelete.addChronicleSegment("A segment to remove");
        adventureToKeep.addChronicleSegment("A segment to keep");

        update(adventureToDelete, adventureToDelete.getId(), Adventure.class);
        update(adventureToKeep, adventureToKeep.getId(), Adventure.class);

        // when
        repository.deleteByPublicId(adventureToDelete.getPublicId());
        entityManager.flush();

        // then
        assertThat(chronicleSegmentsOf(adventureToDelete)).isEmpty();
        assertThat(chronicleSegmentsOf(adventureToKeep)).containsExactly("A segment to keep");
    }

    @Test
    public void shouldRejectDuplicateRosterRowForTheSameAdventureAndCharacter() {

        // given
        var owner = insertUser("11111", "player.one");
        var character = insertCharacter(owner, "Enrolled");
        var adventure = insertAdventureWithRoster(character);

        // then
        assertThrows(Exception.class, () -> {
            jdbcClient
                    .sql("""
                            INSERT INTO adventure_membership (adventure_id, player_character_id, player_id)
                            VALUES (:a, :c, :p)
                            """)
                    .param("a", adventure.getId())
                    .param("c", character.getId())
                    .param("p", owner.getId())
                    .update();
        });
    }

    @Test
    public void shouldRejectASecondCharacterOwnedByTheSamePlayer() {

        // given
        var owner = insertUser("11111", "player.one");
        var character = insertCharacter(owner, "Enrolled");
        var otherCharacter = insertCharacter(owner, "Also Mine");
        var adventure = insertAdventureWithRoster(character);

        // then
        assertThrows(Exception.class, () -> {
            jdbcClient
                    .sql("""
                            INSERT INTO adventure_membership (adventure_id, player_character_id, player_id)
                            VALUES (:a, :c, :p)
                            """)
                    .param("a", adventure.getId())
                    .param("c", otherCharacter.getId())
                    .param("p", owner.getId())
                    .update();
        });
    }

    private User insertUser(String discordId, String username) {

        return insert(UserFixture.player()
                .discordId(discordId)
                .username(username)
                .build(), User.class);
    }

    private PlayerCharacter insertCharacter(User owner, String name) {

        return insert(PlayerCharacterFixture.samplePlayerCharacter()
                .name(name)
                .playerId(owner.getId())
                .build(), PlayerCharacter.class);
    }

    private Adventure insertAdventureWithRoster(PlayerCharacter... characters) {

        var adventure = insert(AdventureFixture.publicSingleplayerAdventure()
                .worldId(world.getPublicId())
                .build(), Adventure.class);

        if (characters.length == 0) {
            return adventure;
        }

        for (var character : characters) {
            adventure.enrollPlayerCharacter(character.getId(), character.getPlayerId());
        }

        update(adventure, adventure.getId(), Adventure.class);

        return adventure;
    }

    private Adventure insertAdventureWithPermissions(Permission... permissions) {

        var adventure = AdventureFixture.publicSingleplayerAdventure()
                .worldId(world.getPublicId())
                .permissions(permissions)
                .build();

        return insert(adventure, Adventure.class);
    }

    @Test
    public void shouldFindAdventureByInvitationPublicId() {

        // given
        var recipient = insertUser("55555", "invitee");
        var adventure = insert(AdventureFixture.publicSingleplayerAdventure()
                .worldId(world.getPublicId())
                .build(), Adventure.class);
        var invitation = adventure.invite(recipient.getId());
        invitation.setCreationDate(java.time.Instant.now());
        update(adventure, adventure.getId(), Adventure.class);

        // when
        var result = repository.findByInvitationPublicId(invitation.getPublicId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getPublicId()).isEqualTo(adventure.getPublicId());
    }

    @Test
    public void shouldReturnEmptyWhenInvitationPublicIdIsUnknown() {

        // when
        var result = repository.findByInvitationPublicId(java.util.UUID.randomUUID());

        // then
        assertThat(result).isEmpty();
    }

    private List<String> chronicleSegmentsOf(Adventure adventure) {

        return jdbcClient.sql("SELECT content FROM chronicle_segment WHERE adventure_id = :adventureId")
                .param("adventureId", adventure.getId())
                .query(String.class)
                .list();
    }

    private List<Long> rosterOf(Adventure adventure) {

        return jdbcClient.sql("SELECT player_character_id FROM adventure_membership WHERE adventure_id = :adventureId")
                .param("adventureId", adventure.getId())
                .query(Long.class)
                .list();
    }
}
