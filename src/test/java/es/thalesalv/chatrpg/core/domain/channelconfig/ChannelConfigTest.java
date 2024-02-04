package es.thalesalv.chatrpg.core.domain.channelconfig;

import static es.thalesalv.chatrpg.core.domain.Visibility.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import es.thalesalv.chatrpg.common.exception.BusinessRuleViolationException;
import es.thalesalv.chatrpg.core.domain.Permissions;
import es.thalesalv.chatrpg.core.domain.PermissionsFixture;
import es.thalesalv.chatrpg.core.domain.Visibility;

public class ChannelConfigTest {

    @Test
    public void createChannelConfig() {

        // Given
        ChannelConfig.Builder channelConfigBuilder = ChannelConfig.builder();
        channelConfigBuilder.name("Name");
        channelConfigBuilder.worldId("WRLDID");
        channelConfigBuilder.personaId("PRSNID");
        channelConfigBuilder.moderation(Moderation.STRICT);
        channelConfigBuilder.visibility(Visibility.fromString("PRIVATE"));
        channelConfigBuilder.modelConfiguration(ModelConfigurationFixture.sample().build());
        channelConfigBuilder.permissions(PermissionsFixture.samplePermissions().build());

        // When
        ChannelConfig channelConfig = channelConfigBuilder.build();

        // Then
        assertThat(channelConfig).isNotNull();
        assertThat(channelConfig.getName()).isEqualTo("Name");
        assertThat(channelConfig.getWorldId()).isEqualTo("WRLDID");
        assertThat(channelConfig.getPersonaId()).isEqualTo("PRSNID");
        assertThat(channelConfig.getModeration()).isEqualTo(Moderation.STRICT);
        assertThat(channelConfig.getVisibility()).isEqualTo(PRIVATE);
    }

    @Test
    public void errorWhenNameIsNull() {

        // Given
        ChannelConfig.Builder channelConfigBuilder = ChannelConfigFixture.sample().name(null);

        // Then
        assertThrows(BusinessRuleViolationException.class, channelConfigBuilder::build);
    }

    @Test
    public void errorWhenNameIsEmpty() {

        // Given
        ChannelConfig.Builder channelConfigBuilder = ChannelConfigFixture.sample().name(StringUtils.EMPTY);

        // Then
        assertThrows(BusinessRuleViolationException.class, channelConfigBuilder::build);
    }

    @Test
    public void errorWhenNameIsBlank() {

        // Given
        ChannelConfig.Builder channelConfigBuilder = ChannelConfigFixture.sample().name(StringUtils.SPACE);

        // Then
        assertThrows(BusinessRuleViolationException.class, channelConfigBuilder::build);
    }

    @Test
    public void errorWhenModelConfigurationIsNull() {

        // Given
        ChannelConfig.Builder channelConfigBuilder = ChannelConfigFixture.sample().modelConfiguration(null);

        // Then
        assertThrows(BusinessRuleViolationException.class, channelConfigBuilder::build);
    }

    @Test
    public void errorWhenModerationIsNull() {

        // Given
        ChannelConfig.Builder channelConfigBuilder = ChannelConfigFixture.sample().moderation(null);

        // Then
        assertThrows(BusinessRuleViolationException.class, channelConfigBuilder::build);
    }

    @Test
    public void errorWhenVisibilityIsNull() {

        // Given
        ChannelConfig.Builder channelConfigBuilder = ChannelConfigFixture.sample().visibility(null);

        // Then
        assertThrows(BusinessRuleViolationException.class, channelConfigBuilder::build);
    }

    @Test
    public void errorWhenPermissionsIsNull() {

        // Given
        ChannelConfig.Builder channelConfigBuilder = ChannelConfigFixture.sample().permissions(null);

        // Then
        assertThrows(BusinessRuleViolationException.class, channelConfigBuilder::build);
    }

    @Test
    public void addWriterToList() {

        // Given
        String userId = "1234567890";
        ChannelConfig.Builder channelConfigBuilder = ChannelConfigFixture.sample();
        Permissions permissions = PermissionsFixture.samplePermissions()
                .usersAllowedToWrite(new ArrayList<>()).build();

        channelConfigBuilder.permissions(permissions);

        ChannelConfig channelConfig = channelConfigBuilder.build();

        // When
        channelConfig.addWriterUser(userId);

        // Then
        assertThat(channelConfig.getWriterUsers()).contains(userId);
    }

    @Test
    public void addReaderToList() {

        // Given
        String userId = "1234567890";
        ChannelConfig.Builder channelConfigBuilder = ChannelConfigFixture.sample();
        Permissions permissions = PermissionsFixture.samplePermissions()
                .usersAllowedToRead(new ArrayList<>()).build();

        channelConfigBuilder.permissions(permissions);

        ChannelConfig channelConfig = channelConfigBuilder.build();

        // When
        channelConfig.addReaderUser(userId);

        // Then
        assertThat(channelConfig.getReaderUsers()).contains(userId);
    }

    @Test
    public void removeReaderFromList() {

        // Given
        String userId = "1234567890";
        ChannelConfig.Builder channelConfigBuilder = ChannelConfigFixture.sample();

        List<String> usersAllowedToRead = new ArrayList<>();
        usersAllowedToRead.add(userId);

        Permissions permissions = PermissionsFixture.samplePermissions()
                .usersAllowedToRead(usersAllowedToRead).build();

        channelConfigBuilder.permissions(permissions);

        ChannelConfig channelConfig = channelConfigBuilder.build();

        // When
        channelConfig.removeReaderUser(userId);

        // Then
        assertThat(channelConfig.getReaderUsers()).doesNotContain(userId);
    }

    @Test
    public void removeWriterFromList() {

        // Given
        String userId = "1234567890";
        ChannelConfig.Builder channelConfigBuilder = ChannelConfigFixture.sample();

        List<String> usersAllowedToWrite = new ArrayList<>();
        usersAllowedToWrite.add(userId);

        Permissions permissions = PermissionsFixture.samplePermissions()
                .usersAllowedToWrite(usersAllowedToWrite).build();

        channelConfigBuilder.permissions(permissions);

        ChannelConfig channelConfig = channelConfigBuilder.build();

        // When
        channelConfig.removeWriterUser(userId);

        // Then
        assertThat(channelConfig.getWriterUsers()).doesNotContain(userId);
    }

    @Test
    public void makeChannelConfigPublic() {

        // Given
        ChannelConfig channelConfig = ChannelConfigFixture.sample().build();

        // When
        channelConfig.makePublic();

        // Then
        assertThat(channelConfig.isPublic()).isTrue();
    }

    @Test
    public void makeChannelConfigPrivate() {

        // Given
        ChannelConfig channelConfig = ChannelConfigFixture.sample().build();

        // When
        channelConfig.makePrivate();

        // Then
        assertThat(channelConfig.isPublic()).isFalse();
    }

    @Test
    public void updateChannelConfigName() {

        // Given
        String name = "New Name";
        ChannelConfig channelConfig = ChannelConfigFixture.sample().build();

        // When
        channelConfig.updateName(name);

        // Then
        assertThat(channelConfig.getName()).isEqualTo(name);
    }

    @Test
    public void updatePersona() {

        // Given
        String personaId = "PRSNID";
        ChannelConfig channelConfig = ChannelConfigFixture.sample().build();

        // When
        channelConfig.updatePersona(personaId);

        // Then
        assertThat(channelConfig.getPersonaId()).isEqualTo(personaId);
    }
}
