package me.moirai.storyengine.core.domain.userdetails;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class UserTest {

    private static final Long NUMERIC_ID = 42L;

    @Test
    public void shouldEmitUserDeletedEventWhenDeletionIsCommunicated() {

        // given
        var user = userWithId();

        // when
        user.communicateUserDeleted();

        // then
        assertThat(user.drainEvents())
                .singleElement()
                .isInstanceOf(UserDeletedEvent.class);
    }

    @Test
    public void shouldCarryTheUserIdentityOnTheDeletionEvent() {

        // given
        var user = userWithId();

        // when
        user.communicateUserDeleted();

        // then
        var event = (UserDeletedEvent) user.drainEvents().getFirst();

        assertThat(event.getUserId()).isEqualTo(NUMERIC_ID);
        assertThat(event.getUserPublicId()).isEqualTo(user.getPublicId());
        assertThat(event.getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    public void shouldReturnNoEventsWhenNothingHasBeenCommunicated() {

        // given
        var user = userWithId();

        // then
        assertThat(user.drainEvents()).isEmpty();
    }

    @Test
    public void shouldClearEventsOnceDrainedWhenDrainedTwice() {

        // given
        var user = userWithId();
        user.communicateUserDeleted();

        // when
        user.drainEvents();

        // then
        assertThat(user.drainEvents()).isEmpty();
    }

    private User userWithId() {

        var user = UserFixture.player().build();
        ReflectionTestUtils.setField(user, "id", NUMERIC_ID);
        ReflectionTestUtils.setField(user, "publicId", UUID.randomUUID());

        return user;
    }
}
