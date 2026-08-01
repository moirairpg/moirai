package me.moirai.storyengine.infrastructure.outbound.adapter.adventure;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import me.moirai.storyengine.core.domain.adventure.Adventure;
import me.moirai.storyengine.core.port.outbound.adventure.AdventureRepository;

@Repository
public class AdventureRepositoryImpl implements AdventureRepository {

    private final AdventureJpaRepository jpaRepository;

    public AdventureRepositoryImpl(AdventureJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Adventure save(Adventure adventure) {

        return jpaRepository.save(adventure);
    }

    @Override
    public void deleteByPublicId(UUID publicId) {

        jpaRepository.deleteByPublicId(publicId);
    }

    @Override
    public void updateSceneByPublicId(String scene, UUID publicId) {

        jpaRepository.updateSceneByPublicId(scene, publicId);
    }

    @Override
    public void updateAuthorsNoteByPublicId(String authorsNote, UUID publicId) {

        jpaRepository.updateAuthorsNoteByPublicId(authorsNote, publicId);
    }

    @Override
    public void updateNudgeByPublicId(String nudge, UUID publicId) {

        jpaRepository.updateNudgeByPublicId(nudge, publicId);
    }

    @Override
    public void updateBumpByPublicId(String bumpContent, int bumpFrequency, UUID publicId) {

        jpaRepository.updateBumpByPublicId(bumpContent, bumpFrequency, publicId);
    }

    @Override
    public Optional<Adventure> findByPublicId(UUID publicId) {

        return jpaRepository.findByPublicId(publicId);
    }

    @Override
    public Optional<Adventure> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Adventure> findAllContainingCharacter(Long playerCharacterId) {

        return jpaRepository.findAllContainingCharacter(playerCharacterId);
    }

    @Override
    public List<Adventure> findAllOwnedBy(Long userId) {

        return jpaRepository.findAllOwnedBy(userId);
    }

    @Override
    public List<Adventure> findAllInvolving(Long userId) {

        return jpaRepository.findAllInvolving(userId);
    }

    @Override
    public List<Long> findManagerUserIdsByAdventureId(Long adventureId) {

        return jpaRepository.findManagerUserIdsByAdventureId(adventureId);
    }

    @Override
    public Optional<String> findEnrolledCharacterName(Long adventureId, String username) {

        return jpaRepository.findEnrolledCharacterName(adventureId, username);
    }

    @Override
    public Optional<Adventure> findByInvitationPublicId(UUID invitationPublicId) {

        return jpaRepository.findByInvitationsPublicId(invitationPublicId);
    }
}
