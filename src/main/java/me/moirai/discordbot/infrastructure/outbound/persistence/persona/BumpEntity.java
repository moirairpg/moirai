package me.moirai.discordbot.infrastructure.outbound.persistence.persona;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class BumpEntity {

    @Column(name = "bump_content")
    private String content;

    @Column(name = "bump_frequency")
    private int frequency;

    @Column(name = "bump_role")
    private String role;

    protected BumpEntity() {
    }

    public BumpEntity(Builder builder) {
        this.content = builder.content;
        this.frequency = builder.frequency;
        this.role = builder.role;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getContent() {
        return content;
    }

    public int getFrequency() {
        return frequency;
    }

    public String getRole() {
        return role;
    }

    public static final class Builder {

        private String content;
        private int frequency;
        private String role;

        private Builder() {
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder frequency(int frequency) {
            this.frequency = frequency;
            return this;
        }

        public Builder role(String role) {
            this.role = role;
            return this;
        }

        public BumpEntity build() {
            return new BumpEntity(this);
        }
    }
}
