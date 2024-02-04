package es.thalesalv.chatrpg.core.domain.persona;

import es.thalesalv.chatrpg.common.exception.BusinessRuleViolationException;
import es.thalesalv.chatrpg.core.domain.CompletionRole;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public final class Bump {

    private final String content;
    private final Integer frequency;
    private final CompletionRole role;

    private Bump(Builder builder) {

        this.content = builder.content;
        this.frequency = builder.frequency;
        this.role = builder.role;
    }

    public static Builder builder() {

        return new Builder();
    }

    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Builder {

        private String content;
        private Integer frequency;
        private CompletionRole role;

        public Builder content(String content) {

            this.content = content;
            return this;
        }

        public Builder frequency(Integer frequency) {

            this.frequency = frequency;
            return this;
        }

        public Builder role(String role) {

            this.role = CompletionRole.fromString(role);
            return this;
        }

        public Bump build() {

            if (frequency < 1) {
                throw new BusinessRuleViolationException("Bump frequency needs to be 1 or greater");
            }

            return new Bump(this);
        }
    }
}
