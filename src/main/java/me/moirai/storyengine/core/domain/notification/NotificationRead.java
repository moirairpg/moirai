package me.moirai.storyengine.core.domain.notification;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import me.moirai.storyengine.common.exception.BusinessRuleViolationException;
import me.moirai.storyengine.common.util.Functions;

@Entity
@Table(name = "notification_read")
public class NotificationRead {

    @EmbeddedId
    private NotificationReadId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("notificationId")
    @JoinColumn(name = "notification_id")
    private Notification notification;

    @Column(name = "read_date")
    private Instant readDate;

    protected NotificationRead() {
        super();
    }

    private NotificationRead(Builder builder) {
        super();
        this.notification = builder.notification;
        this.readDate = builder.readDate;
        this.id = new NotificationReadId(null, builder.userId);
    }

    public static Builder builder() {
        return new Builder();
    }

    public Notification getNotification() {
        return notification;
    }

    public Long getNotificationId() {
        return Functions.mapOrNull(notification, Notification::getId);
    }

    public Long getUserId() {
        return id.userId();
    }

    public Instant getReadDate() {
        return readDate;
    }

    public static final class Builder {

        private Notification notification;
        private Long userId;
        private Instant readDate;

        private Builder() {
        }

        public Builder notification(Notification notification) {
            this.notification = notification;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder readDate(Instant readDate) {
            this.readDate = readDate;
            return this;
        }

        public NotificationRead build() {

            if (notification == null) {
                throw new BusinessRuleViolationException("Notification is required");
            }

            if (userId == null) {
                throw new BusinessRuleViolationException("User ID is required");
            }

            if (readDate == null) {
                throw new BusinessRuleViolationException("Read timestamp is required");
            }

            return new NotificationRead(this);
        }
    }
}
