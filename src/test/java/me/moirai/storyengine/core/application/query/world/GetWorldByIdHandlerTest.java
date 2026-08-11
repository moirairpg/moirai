package me.moirai.storyengine.core.application.query.world;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.storyengine.common.dto.PermissionDto;
import me.moirai.storyengine.common.enums.PermissionLevel;
import me.moirai.storyengine.common.exception.NotFoundException;
import me.moirai.storyengine.core.domain.world.WorldFixture;
import me.moirai.storyengine.core.port.inbound.world.GetWorldById;
import me.moirai.storyengine.core.port.inbound.world.WorldDetails;
import me.moirai.storyengine.core.port.outbound.storage.StoragePort;
import me.moirai.storyengine.core.port.outbound.world.WorldDetailsRow;
import me.moirai.storyengine.core.port.outbound.world.WorldReader;

@ExtendWith(MockitoExtension.class)
public class GetWorldByIdHandlerTest {

    private static final UUID REQUESTER_ID = UUID.fromString("11111111-2222-3333-4444-555555555555");
    private static final UUID OTHER_USER_ID = UUID.fromString("99999999-8888-7777-6666-555555555555");

    @Mock
    private WorldReader reader;

    @Mock
    private StoragePort storagePort;

    @InjectMocks
    private GetWorldByIdHandler handler;

    @Test
    public void errorWhenQueryIsNull() {

        // Given
        GetWorldById query = null;

        // Then
        assertThrows(IllegalArgumentException.class, () -> handler.handle(query));
    }

    @Test
    public void getWorldById() {

        // Given
        var expectedDetails = new WorldDetailsRow(
                WorldFixture.PUBLIC_ID,
                "MoirAI",
                "desc",
                "start",
                null,
                null,
                "PUBLIC",
                null,
                Set.of(),
                Set.of(),
                null,
                null,
                null,
                null);

        var query = new GetWorldById(WorldFixture.PUBLIC_ID, REQUESTER_ID);

        when(reader.getWorldById(any(UUID.class))).thenReturn(Optional.of(expectedDetails));

        // When
        WorldDetails result = handler.handle(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(WorldFixture.PUBLIC_ID);
    }

    @Test
    public void updateWorld_whenIdIsNull_thenExceptionIsThrown() {

        // Given
        var command = new GetWorldById(null, REQUESTER_ID);

        // Then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> handler.handle(command));
    }

    @Test
    public void updateWorld_whenWorldNotFound_thenExceptionIsThrown() {

        // Given
        var command = new GetWorldById(WorldFixture.PUBLIC_ID, REQUESTER_ID);

        when(reader.getWorldById(any(UUID.class))).thenReturn(Optional.empty());

        // Then
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> handler.handle(command));
    }

    @Test
    public void shouldReturnBothFlagsAsTrueWhenRequesterIsTheOwner() {

        // Given
        var row = worldRowWith(Set.of(new PermissionDto(REQUESTER_ID, PermissionLevel.OWNER)));
        var query = new GetWorldById(WorldFixture.PUBLIC_ID, REQUESTER_ID);

        when(reader.getWorldById(any(UUID.class))).thenReturn(Optional.of(row));

        // When
        var result = handler.handle(query);

        // Then
        assertThat(result.canManage()).isTrue();
        assertThat(result.isOwner()).isTrue();
    }

    @Test
    public void shouldReturnCanManageWithoutOwnershipWhenRequesterHasWritePermission() {

        // Given
        var row = worldRowWith(Set.of(
                new PermissionDto(OTHER_USER_ID, PermissionLevel.OWNER),
                new PermissionDto(REQUESTER_ID, PermissionLevel.WRITE)));

        var query = new GetWorldById(WorldFixture.PUBLIC_ID, REQUESTER_ID);

        when(reader.getWorldById(any(UUID.class))).thenReturn(Optional.of(row));

        // When
        var result = handler.handle(query);

        // Then
        assertThat(result.canManage()).isTrue();
        assertThat(result.isOwner()).isFalse();
    }

    @Test
    public void shouldReturnBothFlagsAsFalseWhenRequesterHasReadPermission() {

        // Given
        var row = worldRowWith(Set.of(
                new PermissionDto(OTHER_USER_ID, PermissionLevel.OWNER),
                new PermissionDto(REQUESTER_ID, PermissionLevel.READ)));

        var query = new GetWorldById(WorldFixture.PUBLIC_ID, REQUESTER_ID);

        when(reader.getWorldById(any(UUID.class))).thenReturn(Optional.of(row));

        // When
        var result = handler.handle(query);

        // Then
        assertThat(result.canManage()).isFalse();
        assertThat(result.isOwner()).isFalse();
    }

    @Test
    public void shouldReturnBothFlagsAsFalseWhenRequesterHasNoPermissionEntry() {

        // Given
        var row = worldRowWith(Set.of(new PermissionDto(OTHER_USER_ID, PermissionLevel.OWNER)));
        var query = new GetWorldById(WorldFixture.PUBLIC_ID, REQUESTER_ID);

        when(reader.getWorldById(any(UUID.class))).thenReturn(Optional.of(row));

        // When
        var result = handler.handle(query);

        // Then
        assertThat(result.canManage()).isFalse();
        assertThat(result.isOwner()).isFalse();
    }

    private WorldDetailsRow worldRowWith(Set<PermissionDto> permissions) {

        return new WorldDetailsRow(
                WorldFixture.PUBLIC_ID,
                "MoirAI",
                "desc",
                "start",
                null,
                null,
                "PUBLIC",
                null,
                permissions,
                Set.of(),
                null,
                null,
                null,
                null);
    }
}
