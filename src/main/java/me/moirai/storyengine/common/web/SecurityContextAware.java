package me.moirai.storyengine.common.web;

import java.util.UUID;

import me.moirai.storyengine.common.security.authentication.MoiraiPrincipal;
import me.moirai.storyengine.common.security.authentication.MoiraiSecurityContext;

public abstract class SecurityContextAware {

    protected MoiraiPrincipal getAuthenticatedUser() {
        return MoiraiSecurityContext.getAuthenticatedUser();
    }

    protected UUID authenticatedUserId() {
        return getAuthenticatedUser().publicId();
    }

    protected String authenticatedUsername() {
        return getAuthenticatedUser().username();
    }
}
