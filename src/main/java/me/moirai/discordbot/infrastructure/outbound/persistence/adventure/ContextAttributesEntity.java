package me.moirai.discordbot.infrastructure.outbound.persistence.adventure;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ContextAttributesEntity {

    @Column(name = "nudge")
    private String nudge;

    @Column(name = "authors_note")
    private String authorsNote;

    @Column(name = "remember")
    private String remember;

    @Column(name = "bump")
    private String bump;

    @Column(name = "bump_frequency")
    private Integer bumpFrequency;

    protected ContextAttributesEntity() {
    }

    public ContextAttributesEntity(Builder builder) {
        this.nudge = builder.nudge;
        this.authorsNote = builder.authorsNote;
        this.remember = builder.remember;
        this.bump = builder.bump;
        this.bumpFrequency = builder.bumpFrequency;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getNudge() {
        return nudge;
    }

    public String getAuthorsNote() {
        return authorsNote;
    }

    public String getRemember() {
        return remember;
    }

    public String getBump() {
        return bump;
    }

    public Integer getBumpFrequency() {
        return bumpFrequency;
    }

    public static final class Builder {

        private String nudge;
        private String authorsNote;
        private String remember;
        private String bump;
        private Integer bumpFrequency;

        private Builder() {
        }

        public Builder nudge(String nudge) {
            this.nudge = nudge;
            return this;
        }

        public Builder authorsNote(String authorsNote) {
            this.authorsNote = authorsNote;
            return this;
        }

        public Builder remember(String remember) {
            this.remember = remember;
            return this;
        }

        public Builder bump(String bump) {
            this.bump = bump;
            return this;
        }

        public Builder bumpFrequency(Integer bumpFrequency) {
            this.bumpFrequency = bumpFrequency;
            return this;
        }

        public ContextAttributesEntity build() {
            return new ContextAttributesEntity(this);
        }
    }
}
