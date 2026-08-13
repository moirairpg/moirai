package me.moirai.storyengine.infrastructure.inbound.websocket.controller;

import java.util.List;

import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import me.moirai.storyengine.common.cqs.query.QueryRunner;
import me.moirai.storyengine.common.web.SecurityContextAware;
import me.moirai.storyengine.core.port.inbound.notification.GetActiveBroadcastNotifications;
import me.moirai.storyengine.core.port.inbound.notification.GetActiveSystemNotifications;
import me.moirai.storyengine.core.port.inbound.notification.NotificationDetails;

@Controller
public class NotificationWebSocketController extends SecurityContextAware {

    private final QueryRunner queryRunner;

    public NotificationWebSocketController(QueryRunner queryRunner) {
        this.queryRunner = queryRunner;
    }

    @SubscribeMapping("/notifications/broadcast")
    public List<NotificationDetails> onBroadcastSubscribe() {

        return queryRunner.run(new GetActiveBroadcastNotifications(authenticatedUsername()));
    }

    @SubscribeMapping("/notifications/system")
    public List<NotificationDetails> onSystemSubscribe() {

        return queryRunner.run(new GetActiveSystemNotifications(authenticatedUsername()));
    }
}
