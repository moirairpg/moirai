package es.thalesalv.chatrpg.core.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import es.thalesalv.chatrpg.common.exception.BusinessException;

public class PermissionsTest {

    @Test
    public void errorWhenModifyingWritingUsersListDirectly() {

        // Given
        Permissions permissions = PermissionFixture.samplePermissions().build();

        // Then
        assertThrows(UnsupportedOperationException.class, () -> permissions.getUsersAllowedToWrite().add("613226587696519"));
    }

    @Test
    public void errorWhenModifyingReadingUsersListDirectly() {

        // Given
        Permissions permissions = PermissionFixture.samplePermissions().build();

        // Then
        assertThrows(UnsupportedOperationException.class, () -> permissions.getUsersAllowedToRead().add("613226587696519"));
    }

    @Test
    public void updatePermissionOwner() {

        // Given
        String currentOwnerId = "586678721356875";
        String newOwnerUserId = "403436669070781";
        Permissions originalPermissions = PermissionFixture.samplePermissions().build();

        // When
        Permissions modifiedPermissions = originalPermissions.updateOwner(newOwnerUserId, currentOwnerId);

        // Then
        assertNotEquals(modifiedPermissions.getOwnerDiscordId(), originalPermissions.getOwnerDiscordId());
        assertEquals(newOwnerUserId, modifiedPermissions.getOwnerDiscordId());
    }

    @Test
    public void allowNewUserToWrite() {

        // Given
        String currentOwnerId = "586678721356875";
        String newUserAllowedToWrite = "403436669070781";
        Permissions originalPermissions = PermissionFixture.samplePermissions().build();

        // When
        Permissions modifiedPermissions = originalPermissions.allowUserToWrite(newUserAllowedToWrite, currentOwnerId);

        // Then
        assertThat(modifiedPermissions).isNotEqualTo(originalPermissions);

        assertThat(modifiedPermissions.getUsersAllowedToWrite())
                .isNotNull()
                .isNotEmpty()
                .contains(newUserAllowedToWrite);

        assertThat(originalPermissions.getUsersAllowedToWrite())
                .isNotNull()
                .isNotEmpty()
                .doesNotContain(newUserAllowedToWrite);
    }

    @Test
    public void allowNewUserToWRead() {

        // Given
        String currentOwnerId = "586678721356875";
        String newUserAllowedToRead = "403436669070781";
        Permissions originalPermissions = PermissionFixture.samplePermissions().build();

        // When
        Permissions modifiedPermissions = originalPermissions.allowUserToRead(newUserAllowedToRead, currentOwnerId);

        // Then
        assertThat(modifiedPermissions).isNotEqualTo(originalPermissions);

        assertThat(modifiedPermissions.getUsersAllowedToRead())
                .isNotNull()
                .isNotEmpty()
                .contains(newUserAllowedToRead);

        assertThat(originalPermissions.getUsersAllowedToRead())
                .isNotNull()
                .isNotEmpty()
                .doesNotContain(newUserAllowedToRead);
    }

    @Test
    public void disallowUserToWrite() {

        // Given
        String currentOwnerId = "586678721356875";
        String userToBeDisallowed = "613226587696519";
        Permissions originalPermissions = PermissionFixture.samplePermissions().build();

        // When
        Permissions modifiedPermissions = originalPermissions.disallowUserToWrite(userToBeDisallowed, currentOwnerId);

        // Then
        assertThat(modifiedPermissions).isNotEqualTo(originalPermissions);

        assertThat(modifiedPermissions.getUsersAllowedToWrite())
                .isNotNull()
                .isNotEmpty()
                .doesNotContain(userToBeDisallowed);
    }

    @Test
    public void disallowUserToRead() {

        // Given
        String currentOwnerId = "586678721356875";
        String userToBeDisallowed = "613226587696519";
        Permissions originalPermissions = PermissionFixture.samplePermissions().build();

        // When
        Permissions modifiedPermissions = originalPermissions.disallowUserToRead(userToBeDisallowed, currentOwnerId);

        // Then
        assertThat(modifiedPermissions).isNotEqualTo(originalPermissions);

        assertThat(modifiedPermissions.getUsersAllowedToRead())
                .isNotNull()
                .isNotEmpty()
                .doesNotContain(userToBeDisallowed);
    }

    @Test
    public void errorWhenAddingWritingUserToNotOwnedAsset() {

        // Given
        String invalidOwnerId = "586678721356665";
        String userToBeAllowed = "613226587696519";
        Permissions permissions = PermissionFixture.samplePermissions().build();

        // Then
        assertThrows(BusinessException.class, () -> permissions.allowUserToWrite(userToBeAllowed, invalidOwnerId));
    }

    @Test
    public void errorWhenAddingReadingUserToNotOwnedAsset() {

        // Given
        String invalidOwnerId = "586678721356665";
        String userToBeAllowed = "613226587696519";
        Permissions permissions = PermissionFixture.samplePermissions().build();

        // Then
        assertThrows(BusinessException.class, () -> permissions.allowUserToRead(userToBeAllowed, invalidOwnerId));
    }

    @Test
    public void errorWhenRemovingWritingUserToNotOwnedAsset() {

        // Given
        String invalidOwnerId = "586678721356665";
        String userToBeAllowed = "613226587696519";
        Permissions permissions = PermissionFixture.samplePermissions().build();

        // Then
        assertThrows(BusinessException.class, () -> permissions.disallowUserToWrite(userToBeAllowed, invalidOwnerId));
    }

    @Test
    public void errorWhenRemovingReadingUserToNotOwnedAsset() {

        // Given
        String invalidOwnerId = "586678721356665";
        String userToBeAllowed = "613226587696519";
        Permissions permissions = PermissionFixture.samplePermissions().build();

        // Then
        assertThrows(BusinessException.class, () -> permissions.disallowUserToRead(userToBeAllowed, invalidOwnerId));
    }

    @Test
    public void validateValidOwnership() {

        // Given
        String currentOwnerId = "586678721356875";
        Permissions permissions = PermissionFixture.samplePermissions().build();

        // When
        boolean isOwner = permissions.isOwner(currentOwnerId);

        // Then
        assertThat(isOwner).isTrue();
    }

    @Test
    public void validateInvalidOwnership() {

        // Given
        String invalidOwnerId = "586678721356665";
        Permissions permissions = PermissionFixture.samplePermissions().build();

        // When
        boolean isOwner = permissions.isOwner(invalidOwnerId);

        // Then
        assertThat(isOwner).isFalse();
    }

    @Test
    public void validateValidWritingRights() {

        // Given
        String discordUserId = "613226587696519";
        Permissions permissions = PermissionFixture.samplePermissions().build();

        // When
        boolean isAllowedToWrite = permissions.isAllowedToWrite(discordUserId);

        // Then
        assertThat(isAllowedToWrite).isTrue();
    }

    @Test
    public void validateInvalidWritingRights() {

        // Given
        String discordUserId = "433226587696544";
        Permissions permissions = PermissionFixture.samplePermissions().build();

        // When
        boolean isAllowedToWrite = permissions.isAllowedToWrite(discordUserId);

        // Then
        assertThat(isAllowedToWrite).isFalse();
    }

    @Test
    public void validateValidReadingRights() {

        // Given
        String discordUserId = "613226587696519";
        Permissions permissions = PermissionFixture.samplePermissions().build();

        // When
        boolean isAllowedToWrite = permissions.isAllowedToRead(discordUserId);

        // Then
        assertThat(isAllowedToWrite).isTrue();
    }

    @Test
    public void validateInvalidReadingRights() {

        // Given
        String discordUserId = "433226587696544";
        Permissions permissions = PermissionFixture.samplePermissions().build();

        // When
        boolean isAllowedToWrite = permissions.isAllowedToRead(discordUserId);

        // Then
        assertThat(isAllowedToWrite).isFalse();
    }

    @Test
    public void validateOwnerWritingRights() {

        // Given
        String ownerDiscordId = "586678721356875";
        Permissions permissions = PermissionFixture.samplePermissions().build();

        // When
        boolean isAllowedToWrite = permissions.isAllowedToWrite(ownerDiscordId);

        // Then
        assertThat(isAllowedToWrite).isTrue();
    }

    @Test
    public void validateOwnerReadingRights() {

        // Given
        String ownerDiscordId = "586678721356875";
        Permissions permissions = PermissionFixture.samplePermissions().build();

        // When
        boolean isAllowedToWrite = permissions.isAllowedToRead(ownerDiscordId);

        // Then
        assertThat(isAllowedToWrite).isTrue();
    }
}
