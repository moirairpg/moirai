package es.thalesalv.chatrpg.core.domain.persona;

import es.thalesalv.chatrpg.common.exception.BusinessRuleViolationException;
import es.thalesalv.chatrpg.core.domain.Permissions;
import es.thalesalv.chatrpg.core.domain.ShareableAsset;
import es.thalesalv.chatrpg.core.domain.Visibility;
import io.micrometer.common.util.StringUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

// TODO implement missing VO methods (manage bump and nudge)
@Getter
public class Persona extends ShareableAsset {

    private String id;
    private String name;
    private String personality;
    private Nudge nudge;
    private Bump bump;

    private Persona(Builder builder) {

        super(builder.permissions, builder.visibility);
        this.id = builder.id;
        this.name = builder.name;
        this.personality = builder.personality;
        this.nudge = builder.nudge;
        this.bump = builder.bump;
    }

    public static Builder builder() {

        return new Builder();
    }

    public void updateName(String name) {

        this.name = name;
    }

    public void updatePersonality(String personality) {

        this.personality = personality;
    }

    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Builder {

        private String id;
        private String name;
        private String personality;
        private Nudge nudge;
        private Bump bump;
        private Visibility visibility;
        private Permissions permissions;

        public Builder id(String id) {

            this.id = id;
            return this;
        }

        public Builder name(String name) {

            this.name = name;
            return this;
        }

        public Builder personality(String personality) {

            this.personality = personality;
            return this;
        }

        public Builder nudge(Nudge nudge) {

            this.nudge = nudge;
            return this;
        }

        public Builder bump(Bump bump) {

            this.bump = bump;
            return this;
        }

        public Builder visibility(Visibility visibility) {

            this.visibility = visibility;
            return this;
        }

        public Builder permissions(Permissions permissions) {

            this.permissions = permissions;
            return this;
        }

        public Persona build() {

            if (StringUtils.isBlank(name)) {
                throw new BusinessRuleViolationException("Persona name cannot be null or empty");
            }

            if (StringUtils.isBlank(personality)) {
                throw new BusinessRuleViolationException("Persona personality cannot be null or empty");
            }

            if (visibility == null) {
                throw new BusinessRuleViolationException("Visibility cannot be null");
            }

            if (permissions == null) {
                throw new BusinessRuleViolationException("Permissions cannot be null");
            }

            return new Persona(this);
        }
    }
}
