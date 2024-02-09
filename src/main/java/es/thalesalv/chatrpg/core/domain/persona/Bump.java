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

    private Builder cloneFrom(Bump bump) {

        return builder()
                .content(bump.content)
                .frequency(bump.frequency)
                .role(bump.role);
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

        public Builder role(CompletionRole role) {

            this.role = role;
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
