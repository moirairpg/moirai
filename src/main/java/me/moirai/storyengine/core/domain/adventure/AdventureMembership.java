package me.moirai.storyengine.core.domain.adventure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import me.moirai.storyengine.common.exception.BusinessRuleViolationException;

@Entity
@Table(name = "adventure_membership")
public class AdventureMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "adventure_id")
    private Long adventureId;

    @Column(name = "player_character_id")
    private Long playerCharacterId;

    @Column(name = "player_id")
    private Long playerId;

    protected AdventureMembership() {
    }

    private AdventureMembership(Long adventureId, Long playerCharacterId, Long playerId) {

        if (adventureId == null) {
            throw new BusinessRuleViolationException("Adventure ID cannot be null");
        }

        if (playerCharacterId == null) {
            throw new BusinessRuleViolationException("Player character ID cannot be null");
        }

        if (playerId == null) {
            throw new BusinessRuleViolationException("Player ID cannot be null");
        }

        this.adventureId = adventureId;
        this.playerCharacterId = playerCharacterId;
        this.playerId = playerId;
    }

    static AdventureMembership of(Long adventureId, Long playerCharacterId, Long playerId) {
        return new AdventureMembership(adventureId, playerCharacterId, playerId);
    }

    public Long getId() {
        return id;
    }

    public Long getPlayerCharacterId() {
        return playerCharacterId;
    }

    public Long getPlayerId() {
        return playerId;
    }
}
