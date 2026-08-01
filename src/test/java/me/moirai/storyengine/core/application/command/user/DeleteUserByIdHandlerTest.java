package me.moirai.storyengine.core.application.command.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import me.moirai.storyengine.common.exception.NotFoundException;
import me.moirai.storyengine.core.domain.userdetails.User;
import me.moirai.storyengine.core.domain.userdetails.UserDeletedEvent;
import me.moirai.storyengine.core.domain.userdetails.UserFixture;
import me.moirai.storyengine.core.port.inbound.userdetails.DeleteUserById;
import me.moirai.storyengine.core.port.outbound.userdetails.UserRepository;

@ExtendWith(MockitoExtension.class)
public class DeleteUserByIdHandlerTest {

    @Mock
    private UserRepository repository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private DeleteUserByIdHandler handler;

    @Test
    public void deleteUser_whenIdIsNull_thenThrowException() {

        // Given
        DeleteUserById command = new DeleteUserById(null);

        // Then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> handler.handle(command));
    }

    @Test
    public void deleteUser_whenUserNotFound_thenThrowException() {

        // Given
        DeleteUserById command = new DeleteUserById(UUID.randomUUID());

        when(repository.findByPublicId(any(UUID.class))).thenReturn(Optional.empty());

        // Then
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> handler.handle(command));
    }

    @Test
    public void deleteUser_whenValidRequest_thenUserIsDeleted() {

        // Given
        DeleteUserById command = new DeleteUserById(UUID.randomUUID());
        User user = UserFixture.player().build();

        when(repository.findByPublicId(any(UUID.class))).thenReturn(Optional.of(user));

        // When
        handler.handle(command);

        // Then
        verify(repository, times(1)).delete(user);
    }

    @Test
    public void shouldPublishUserDeletedEventCarryingTheUserIdentityWhenTheUserIsDeleted() {

        // given
        var command = new DeleteUserById(UUID.randomUUID());
        var user = UserFixture.player().build();
        var publicId = UUID.randomUUID();

        ReflectionTestUtils.setField(user, "id", 42L);
        ReflectionTestUtils.setField(user, "publicId", publicId);

        when(repository.findByPublicId(any(UUID.class))).thenReturn(Optional.of(user));

        // when
        handler.handle(command);

        // then
        var captor = ArgumentCaptor.forClass(UserDeletedEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());

        assertThat(captor.getValue().getUserId()).isEqualTo(42L);
        assertThat(captor.getValue().getUserPublicId()).isEqualTo(publicId);
        assertThat(captor.getValue().getUsername()).isEqualTo(user.getUsername());
    }
}
