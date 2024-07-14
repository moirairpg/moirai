package es.thalesalv.chatrpg.infrastructure.outbound.persistence.persona;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class NudgeEntity {

    @Column(name = "nudge_content")
    private String content;

    @Column(name = "nudge_role")
    private String role;

    protected NudgeEntity() {
    }

    public NudgeEntity(Builder builder) {
        this.content = builder.content;
        this.role = builder.role;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getContent() {
        return content;
    }

    public String getRole() {
        return role;
    }

    public static final class Builder {

        private String content;
        private String role;

        private Builder() {
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder role(String role) {
            this.role = role;
            return this;
        }

        public NudgeEntity build() {
            return new NudgeEntity(this);
        }
    }
}
