package me.moirai.storyengine.infrastructure.security.authorization.notification;

import static me.moirai.storyengine.common.enums.NotificationType.BROADCAST;
import static me.moirai.storyengine.common.enums.Role.ADMIN;

import org.springframework.stereotype.Component;

import me.moirai.storyengine.common.security.authentication.MoiraiPrincipal;
import me.moirai.storyengine.common.security.authorization.AuthorizationContext;
import me.moirai.storyengine.common.security.authorization.AuthorizationOperation;
import me.moirai.storyengine.common.security.authorization.OperationAuthorizer;
import me.moirai.storyengine.core.port.inbound.notification.NotificationBasicData;
import me.moirai.storyengine.core.port.outbound.notification.NotificationBasicDataReader;

@Component
public class ViewNotificationAuthorizer implements OperationAuthorizer {

    private final NotificationBasicDataReader reader;

    public ViewNotificationAuthorizer(NotificationBasicDataReader reader) {
        this.reader = reader;
    }

    @Override
    public AuthorizationOperation getOperation() {
        return AuthorizationOperation.VIEW_NOTIFICATION;
    }

    @Override
    public boolean authorize(AuthorizationContext context) {

        var notificationId = context.getFieldAsUuid("notificationId");
        var principal = context.getPrincipal();

        return reader.getByPublicId(notificationId)
                .map(basicData -> canView(basicData, principal))
                .orElse(false);
    }

    private boolean canView(NotificationBasicData basicData, MoiraiPrincipal principal) {

        if (principal.role() == ADMIN) {
            return true;
        }

        if (!basicData.targetUsernames().isEmpty()) {
            return basicData.targetUsernames().contains(principal.username());
        }

        return basicData.type() == BROADCAST;
    }
}
