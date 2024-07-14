package es.thalesalv.chatrpg.infrastructure.security.authentication;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public final class DiscordPrincipal implements UserDetails {

    private final String id;
    private final String username;
    private final String email;
    private final String authorizationToken;

    private DiscordPrincipal(Builder builder) {
        this.id = builder.id;
        this.username = builder.username;
        this.email = builder.email;
        this.authorizationToken = builder.authorizationToken;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getAuthorizationToken() {
        return authorizationToken;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public static class Builder {

        private String id;
        private String username;
        private String email;
        private String authorizationToken;

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

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder authorizationToken(String authorizationToken) {
            this.authorizationToken = authorizationToken;
            return this;
        }

        public DiscordPrincipal build() {
            return new DiscordPrincipal(this);
        }
    }
}
