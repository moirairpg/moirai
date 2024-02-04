package es.thalesalv.chatrpg.core.domain.persona;

import es.thalesalv.chatrpg.core.domain.CompletionRole;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
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

    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Builder {

        private String content;
        private CompletionRole role;

        public Builder content(String content) {

            this.content = content;
            return this;
        }

        public Builder role(String role) {

            this.role = CompletionRole.fromString(role);
            return this;
        }

        public Nudge build() {

            return new Nudge(this);
        }
    }
}
