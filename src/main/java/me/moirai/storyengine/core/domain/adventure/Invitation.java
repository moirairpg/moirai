package me.moirai.storyengine.core.domain.adventure;

import java.util.UUID;

import com.fasterxml.uuid.Generators;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import me.moirai.storyengine.common.domain.Asset;
import me.moirai.storyengine.common.enums.InvitationStatus;
import me.moirai.storyengine.common.exception.BusinessRuleViolationException;

@Entity
@Table(name = "adventure_invitation")
public class Invitation extends Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id")
    private UUID publicId;

    @Column(name = "adventure_id")
    private Long adventureId;

    @Column(name = "user_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private InvitationStatus status;

    private Invitation(Builder builder) {

        super();

        this.publicId = Generators.timeBasedEpochGenerator().generate();
        this.adventureId = builder.adventureId;
        this.userId = builder.userId;
        this.status = InvitationStatus.PENDING;
    }

    protected Invitation() {
        super();
    }

    public static Builder builder() {

        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public UUID getPublicId() {
        return publicId;
    }

    public Long getUserId() {
        return userId;
    }

    public InvitationStatus getStatus() {
        return status;
    }

    public boolean isPending() {
        return status == InvitationStatus.PENDING;
    }

    public void accept() {

        if (status != InvitationStatus.PENDING) {
            throw new BusinessRuleViolationException("Invitation has already been answered");
        }

        this.status = InvitationStatus.ACCEPTED;
    }

    public void decline() {

        if (status != InvitationStatus.PENDING) {
            throw new BusinessRuleViolationException("Invitation has already been answered");
        }

        this.status = InvitationStatus.DECLINED;
    }

    public static final class Builder {

        private Long adventureId;
        private Long userId;

        private Builder() {
        }

        public Builder adventureId(Long adventureId) {

            this.adventureId = adventureId;
            return this;
        }

        public Builder userId(Long userId) {

            this.userId = userId;
            return this;
        }

        public Invitation build() {

            if (adventureId == null) {
                throw new BusinessRuleViolationException("Invitation must belong to an adventure");
            }

            if (userId == null) {
                throw new BusinessRuleViolationException("Invitation must have a recipient");
            }

            return new Invitation(this);
        }
    }
}
