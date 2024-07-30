package me.moirai.discordbot.core.domain.persona;

import me.moirai.discordbot.core.domain.CompletionRole;

public final class Bump {

    private final String content;
    private final int frequency;
    private final CompletionRole role;

    private Bump(Builder builder) {

        this.content = builder.content;
        this.frequency = builder.frequency;
        this.role = builder.role;
    }

    public static Builder builder() {

        return new Builder();
    }

    private Builder cloneFrom(Bump bump) {

        return builder()
                .content(bump.content)
                .frequency(bump.frequency)
                .role(bump.role);
    }

    public String getContent() {
        return content;
    }

    public int getFrequency() {
        return frequency;
    }

    public CompletionRole getRole() {
        return role;
    }

    public Bump updateContent(String content) {

        return cloneFrom(this).content(content).build();
    }

    public Bump updateFrequency(int frequency) {

        return cloneFrom(this).frequency(frequency).build();
    }

    public Bump updateRole(CompletionRole role) {

        return cloneFrom(this).role(role).build();
    }

    public static final class Builder {

        private String content;
        private int frequency;
        private CompletionRole role;

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

        public Builder role(CompletionRole role) {

            this.role = role;
            return this;
        }

        public Bump build() {

            return new Bump(this);
        }
    }
}
