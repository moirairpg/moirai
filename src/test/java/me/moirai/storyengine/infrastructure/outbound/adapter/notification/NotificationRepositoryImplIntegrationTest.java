package me.moirai.storyengine.infrastructure.outbound.adapter.notification;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.transaction.Transactional;
import me.moirai.storyengine.AbstractDatabaseIntegrationTest;
import me.moirai.storyengine.core.domain.notification.Notification;
import me.moirai.storyengine.core.domain.notification.NotificationFixture;
import me.moirai.storyengine.core.port.outbound.notification.NotificationRepository;

@Transactional
public class NotificationRepositoryImplIntegrationTest extends AbstractDatabaseIntegrationTest {

    @Autowired
    private NotificationRepository repository;

    @Autowired
    private JdbcClient jdbcClient;

    @BeforeEach
    public void before() {
        clearDatabase();
    }

    @Test
    public void deleteAllGameNotificationsByAdventureId_whenGameNotificationsExist_thenDeleteThem() {

        // given
        var adventureId = 4242L;
        var game = NotificationFixture.game().build();
        ReflectionTestUtils.setField(game, "adventureId", adventureId);
        var inserted = insert(game, Notification.class);

        // when
        repository.deleteAllGameNotificationsByAdventureId(adventureId);

        // then
        assertThat(repository.findByPublicId(inserted.getPublicId())).isEmpty();
    }

    @Test
    public void deleteAllGameNotificationsByAdventureId_whenNoGameNotifications_thenDoNothing() {

        // given
        var adventureId = 9999L;

        // when
        repository.deleteAllGameNotificationsByAdventureId(adventureId);

        // then
        assertThat(repository.findByPublicId(UUID.randomUUID())).isEmpty();
    }

    @Test
    public void shouldReturnTheNotificationWhenTheUserIsOnlyARecipient() {

        // given
        var userId = 111L;
        var notification = insert(NotificationFixture.broadcast().build(), Notification.class);
        addRecipient(notification, userId);

        // when
        var result = repository.findAllInvolving(userId);

        // then
        assertThat(result)
                .extracting(Notification::getPublicId)
                .containsExactly(notification.getPublicId());
    }

    @Test
    public void shouldReturnTheNotificationWhenTheUserOnlyHasAReadMarker() {

        // given
        var userId = 222L;
        var notification = insert(NotificationFixture.broadcast().build(), Notification.class);
        addReadMarker(notification, userId);

        // when
        var result = repository.findAllInvolving(userId);

        // then
        assertThat(result)
                .extracting(Notification::getPublicId)
                .containsExactly(notification.getPublicId());
    }

    @Test
    public void shouldReturnTheNotificationOnceWhenTheUserIsBothRecipientAndReader() {

        // given
        var userId = 333L;
        var notification = insert(NotificationFixture.broadcast().build(), Notification.class);
        addRecipient(notification, userId);
        addReadMarker(notification, userId);

        // when
        var result = repository.findAllInvolving(userId);

        // then
        assertThat(result)
                .extracting(Notification::getPublicId)
                .containsExactly(notification.getPublicId());
    }

    @Test
    public void shouldReturnNothingWhenTheUserIsUnrelatedToAnyNotification() {

        // given
        var notification = insert(NotificationFixture.broadcast().build(), Notification.class);
        addRecipient(notification, 444L);

        // when
        var result = repository.findAllInvolving(555L);

        // then
        assertThat(result).isEmpty();
    }

    private void addRecipient(Notification notification, Long userId) {

        jdbcClient.sql("INSERT INTO notification_recipient (notification_id, user_id) VALUES (:id, :userId)")
                .param("id", notification.getId())
                .param("userId", userId)
                .update();
    }

    private void addReadMarker(Notification notification, Long userId) {

        jdbcClient.sql("INSERT INTO notification_read (notification_id, user_id, read_date) VALUES (:id, :userId, NOW())")
                .param("id", notification.getId())
                .param("userId", userId)
                .update();
    }
}
