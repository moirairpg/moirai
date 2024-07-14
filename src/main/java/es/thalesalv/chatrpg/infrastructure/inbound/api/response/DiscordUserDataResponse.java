package es.thalesalv.chatrpg.infrastructure.inbound.api.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DiscordUserDataResponse {

    @JsonProperty("id")
    private String id;

    @JsonProperty("redirect_uri")
    private String username;

    @JsonProperty("global_name")
    private String globalName;

    @JsonProperty("display_name")
    private String displayName;

    @JsonProperty("avatar")
    private String avatar;

    @JsonProperty("discriminator")
    private String discriminator;

    @JsonProperty("email")
    private String email;

    @JsonProperty("error")
    private DiscordErrorResponse error;

    public DiscordUserDataResponse() {
    }

    private DiscordUserDataResponse(Builder builder) {
        this.id = builder.id;
        this.username = builder.username;
        this.globalName = builder.globalName;
        this.displayName = builder.displayName;
        this.avatar = builder.avatar;
        this.discriminator = builder.discriminator;
        this.email = builder.email;
        this.error = builder.error;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getGlobalName() {
        return globalName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getDiscriminator() {
        return discriminator;
    }

    public String getEmail() {
        return email;
    }

    public DiscordErrorResponse getError() {
        return error;
    }

    public static final class Builder {
        private String id;
        private String username;
        private String globalName;
        private String displayName;
        private String avatar;
        private String discriminator;
        private String email;
        private DiscordErrorResponse error;

        private Builder() {
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder globalName(String globalName) {
            this.globalName = globalName;
            return this;
        }

        public Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder avatar(String avatar) {
            this.avatar = avatar;
            return this;
        }

        public Builder discriminator(String discriminator) {
            this.discriminator = discriminator;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder error(DiscordErrorResponse error) {
            this.error = error;
            return this;
        }

        public DiscordUserDataResponse build() {
            return new DiscordUserDataResponse(this);
        }
    }
}