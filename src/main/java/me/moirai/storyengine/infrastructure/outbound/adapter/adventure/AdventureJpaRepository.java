package me.moirai.storyengine.infrastructure.outbound.adapter.adventure;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import me.moirai.storyengine.common.dbutil.PaginationRepository;
import me.moirai.storyengine.core.domain.adventure.Adventure;

public interface AdventureJpaRepository
        extends JpaRepository<Adventure, Long>, PaginationRepository<Adventure, Long> {

    Optional<Adventure> findByPublicId(UUID publicId);

    void deleteByPublicId(UUID publicId);

    @Modifying
    @Query("UPDATE Adventure a SET a.contextAttributes.scene = :scene WHERE a.publicId = :publicId")
    void updateSceneByPublicId(String scene, UUID publicId);

    @Modifying
    @Query("UPDATE Adventure a SET a.contextAttributes.authorsNote = :authorsNote WHERE a.publicId = :publicId")
    void updateAuthorsNoteByPublicId(String authorsNote, UUID publicId);

    @Modifying
    @Query("UPDATE Adventure a SET a.contextAttributes.nudge = :nudge WHERE a.publicId = :publicId")
    void updateNudgeByPublicId(String nudge, UUID publicId);

    @Modifying
    @Query("UPDATE Adventure a SET a.contextAttributes.bump = :bump, a.contextAttributes.bumpFrequency = :bumpFrequency WHERE a.publicId = :publicId")
    void updateBumpByPublicId(String bump, int bumpFrequency, UUID publicId);

    @Modifying
    @Query("DELETE FROM AdventureMembership m WHERE m.playerCharacterId = :playerCharacterId")
    void removeCharacterFromAllRosters(Long playerCharacterId);

    @Query(value = """
            SELECT ap.user_id
              FROM adventure_permissions ap
             WHERE ap.adventure_id = :adventureId
               AND ap.permission IN ('OWNER', 'WRITE')
            """, nativeQuery = true)
    List<Long> findManagerUserIdsByAdventureId(Long adventureId);

    @Query(value = """
            SELECT pc.name
              FROM adventure_membership am
                   JOIN player_character pc ON pc.id = am.player_character_id
                   JOIN moirai_user u       ON u.id = pc.player_id
             WHERE am.adventure_id = :adventureId
               AND u.username      = :username
            """, nativeQuery = true)
    Optional<String> findEnrolledCharacterName(Long adventureId, String username);

    Optional<Adventure> findByInvitationsPublicId(UUID publicId);
}
