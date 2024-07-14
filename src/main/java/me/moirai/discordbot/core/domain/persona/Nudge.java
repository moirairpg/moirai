package me.moirai.discordbot.core.domain.persona;

import me.moirai.discordbot.core.domain.CompletionRole;

public final class Nudge {

    private final String content;
    private final CompletionRole role;

    private Nudge(Builder builder) {

        this.content = builder.content;
        this.role = builder.role;
    }

    public static Builder builder() {

        return new Builder();
    }

    private Builder cloneFrom(Nudge nudge) {

        return builder()
                .content(nudge.content)
                .role(nudge.role);
    }

    public String getContent() {
        return content;
    }

    public CompletionRole getRole() {
        return role;
    }

    public Nudge updateContent(String content) {

        return cloneFrom(this).content(content).build();
    }

    public Nudge updateRole(CompletionRole role) {

        return cloneFrom(this).role(role).build();
    }

    public static final class Builder {

        private String content;
        private CompletionRole role;

        private Builder() {
        }

        public Builder content(String content) {

            this.content = content;
            return this;
        }

        public Builder role(CompletionRole role) {

            this.role = role;
            return this;
        }

        public Nudge build() {

            return new Nudge(this);
        }
    }
}
