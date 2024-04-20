package es.thalesalv.chatrpg.core.domain.channelconfig;

import static es.thalesalv.chatrpg.core.domain.Visibility.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import es.thalesalv.chatrpg.common.exception.BusinessRuleViolationException;
import es.thalesalv.chatrpg.core.domain.Permissions;
import es.thalesalv.chatrpg.core.domain.PermissionsFixture;
import es.thalesalv.chatrpg.core.domain.Visibility;

public class ChannelConfigTest {

    @Test
    public void createChannelConfig_whenValidData_thenCreateChannelConfig() {

        // Given
        ChannelConfig.Builder channelConfigBuilder = ChannelConfig.builder();
        channelConfigBuilder.name("Name");
        channelConfigBuilder.worldId("WRLDID");
        channelConfigBuilder.personaId("PRSNID");
        channelConfigBuilder.discordChannelId("CHNLID");
        channelConfigBuilder.moderation(Moderation.STRICT);
        channelConfigBuilder.visibility(Visibility.fromString("PRIVATE"));
        channelConfigBuilder.modelConfiguration(ModelConfigurationFixture.gpt3516k().build());
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
    public void createChannelConfig_whenNameIsNull_thenThrowException() {

        // Given
        ChannelConfig.Builder channelConfigBuilder = ChannelConfigFixture.sample().name(null);

        // Then
        assertThrows(BusinessRuleViolationException.class, channelConfigBuilder::build);
    }

    @Test
    public void createChannelConfig_whenNameIsEmpty_thenThrowException() {

        // Given
        ChannelConfig.Builder channelConfigBuilder = ChannelConfigFixture.sample().name(StringUtils.EMPTY);

        // Then
        assertThrows(BusinessRuleViolationException.class, channelConfigBuilder::build);
    }

    @Test
    public void createChannelConfig_whenModelConfigurationIsNull_thenThrowException() {

        // Given
        ChannelConfig.Builder channelConfigBuilder = ChannelConfigFixture.sample().modelConfiguration(null);

        // Then
        assertThrows(BusinessRuleViolationException.class, channelConfigBuilder::build);
    }

    @Test
    public void createChannelConfig_whenModerationIsNull_thenThrowException() {

        // Given
        ChannelConfig.Builder channelConfigBuilder = ChannelConfigFixture.sample().moderation(null);

        // Then
        assertThrows(BusinessRuleViolationException.class, channelConfigBuilder::build);
    }

    @Test
    public void createChannelConfig_whenVisibilityIsNull_thenThrowException() {

        // Given
        ChannelConfig.Builder channelConfigBuilder = ChannelConfigFixture.sample().visibility(null);

        // Then
        assertThrows(BusinessRuleViolationException.class, channelConfigBuilder::build);
    }

    @Test
    public void createChannelConfig_whenPermissionsIsNull_thenThrowException() {

        // Given
        ChannelConfig.Builder channelConfigBuilder = ChannelConfigFixture.sample().permissions(null);

        // Then
        assertThrows(BusinessRuleViolationException.class, channelConfigBuilder::build);
    }

    @Test
    public void updateChannelConfig_whenAddNewWriter_thenTheyShouldHaveReadAndWritePermission() {

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
        assertThat(channelConfig.getUsersAllowedToWrite()).contains(userId);
        assertThat(channelConfig.canUserWrite(userId)).isTrue();
        assertThat(channelConfig.canUserRead(userId)).isTrue();
    }

    @Test
    public void updateChannelConfig_whenAddNewReader_thenTheyShouldHaveOnlyReadPermission() {

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
        assertThat(channelConfig.getUsersAllowedToRead()).contains(userId);
        assertThat(channelConfig.canUserWrite(userId)).isFalse();
        assertThat(channelConfig.canUserRead(userId)).isTrue();
    }

    @Test
    public void updateChannelConfig_whenRemoveReader_thenReadPermissionShouldBeRevoked() {

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
        assertThat(channelConfig.getUsersAllowedToRead()).doesNotContain(userId);
        assertThat(channelConfig.canUserWrite(userId)).isFalse();
        assertThat(channelConfig.canUserRead(userId)).isFalse();
    }

    @Test
    public void updateChannelConfig_whenRemoveWriter_thenReadAndWritePermissionShouldBeRevoked() {

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
        assertThat(channelConfig.getUsersAllowedToWrite()).doesNotContain(userId);
        assertThat(channelConfig.canUserWrite(userId)).isFalse();
        assertThat(channelConfig.canUserRead(userId)).isFalse();
    }

    @Test
    public void updateChannelConfig_whenTurningPrivateIntoPublic_thenPermissionShouldBeChangedToPublic() {

        // Given
        ChannelConfig channelConfig = ChannelConfigFixture.sample().build();

        // When
        channelConfig.makePublic();

        // Then
        assertThat(channelConfig.isPublic()).isTrue();
    }

    @Test
    public void updateChannelConfig_whenTurningPublicIntoPrivate_thenPermissionShouldBeChangedToPrivate() {

        // Given
        ChannelConfig channelConfig = ChannelConfigFixture.sample().build();

        // When
        channelConfig.makePrivate();

        // Then
        assertThat(channelConfig.isPublic()).isFalse();
    }

    @Test
    public void updateChannelConfig_whenNewNameProvided_thenNameShouldBeUpdated() {

        // Given
        String name = "New Name";
        ChannelConfig channelConfig = ChannelConfigFixture.sample().build();

        // When
        channelConfig.updateName(name);

        // Then
        assertThat(channelConfig.getName()).isEqualTo(name);
    }

    @Test
    public void updateChannelConfig_whenNewPersonaIdProvided_thenPersonaIdShouldBeUpdated() {

        // Given
        String personaId = "PRSNID";
        ChannelConfig channelConfig = ChannelConfigFixture.sample().build();

        // When
        channelConfig.updatePersona(personaId);

        // Then
        assertThat(channelConfig.getPersonaId()).isEqualTo(personaId);
    }

    @Test
    public void updateChannelConfig_whenNewModerationProvided_thenModerationShouldBeUpdated() {

        // Given
        Moderation moderation = Moderation.DISABLED;
        ChannelConfig channelConfig = ChannelConfigFixture.sample().build();

        // When
        channelConfig.updateModeration(moderation);

        // Then
        assertThat(channelConfig.getModeration()).isEqualTo(moderation);
    }

    @Test
    public void updateChannelConfig_whenNewAiModelIsProvided_thenAiModelShouldBeUpdated() {

        // Given
        ArtificialIntelligenceModel aiModel = ArtificialIntelligenceModel.GPT4_128K;
        ChannelConfig channelConfig = ChannelConfigFixture.sample().build();

        // When
        channelConfig.updateAiModel(aiModel);

        // Then
        assertThat(channelConfig.getModelConfiguration().getAiModel()).isEqualTo(aiModel);
    }

    @Test
    public void updateChannelConfig_whenNewMaxTokenLimit_thenMaxTokenLimitShouldBeUpdated() {

        // Given
        int maxTokenLimit = 100;
        ArtificialIntelligenceModel aiModel = ArtificialIntelligenceModel.GPT35_16K;
        ModelConfiguration modelConfiguration = ModelConfigurationFixture.gpt3516k().aiModel(aiModel).build();
        ChannelConfig channelConfig = ChannelConfigFixture.sample().modelConfiguration(modelConfiguration).build();

        // When
        channelConfig.updateMaxTokenLimit(maxTokenLimit);

        // Then
        assertThat(channelConfig.getModelConfiguration().getMaxTokenLimit()).isEqualTo(maxTokenLimit);
    }

    @Test
    public void updateChannelConfig_whenNewMaxTokenLimitGreaterThanAllowed_thenThrowException() {

        // Given
        int maxTokenLimit = 50000;
        ArtificialIntelligenceModel aiModel = ArtificialIntelligenceModel.GPT35_16K;
        ModelConfiguration modelConfiguration = ModelConfigurationFixture.gpt3516k().aiModel(aiModel).build();
        ChannelConfig channelConfig = ChannelConfigFixture.sample().modelConfiguration(modelConfiguration).build();

        // Then
        assertThrows(BusinessRuleViolationException.class,
                () -> channelConfig.updateMaxTokenLimit(maxTokenLimit));
    }

    @Test
    public void updateChannelConfig_whenNewMaxTokenLimitLesserThanAllowed_thenThrowException() {

        // Given
        int maxTokenLimit = 10;
        ArtificialIntelligenceModel aiModel = ArtificialIntelligenceModel.GPT35_16K;
        ModelConfiguration modelConfiguration = ModelConfigurationFixture.gpt3516k().aiModel(aiModel).build();
        ChannelConfig channelConfig = ChannelConfigFixture.sample().modelConfiguration(modelConfiguration).build();

        // Then
        assertThrows(BusinessRuleViolationException.class,
                () -> channelConfig.updateMaxTokenLimit(maxTokenLimit));
    }

    @Test
    public void updateChannelConfig_whenNewTemperature_thenTemperatureShouldBeUpdated() {

        // Given
        double temperature = 1.3;
        ChannelConfig channelConfig = ChannelConfigFixture.sample().build();

        // When
        channelConfig.updateTemperature(temperature);

        // Then
        assertThat(channelConfig.getModelConfiguration().getTemperature()).isEqualTo(temperature);
    }

    @Test
    public void updateChannelConfig_whenNewTemperatureGreaterThanAllowed_thenThrowException() {

        // Given
        double temperature = 3;
        ChannelConfig channelConfig = ChannelConfigFixture.sample().build();

        // Then
        assertThrows(BusinessRuleViolationException.class,
                () -> channelConfig.updateTemperature(temperature));
    }

    @Test
    public void updateChannelConfig_whenNewTemperatureLesserThanAllowed_thenThrowException() {

        // Given
        double temperature = 0;
        ChannelConfig channelConfig = ChannelConfigFixture.sample().build();

        // Then
        assertThrows(BusinessRuleViolationException.class,
                () -> channelConfig.updateTemperature(temperature));
    }

    @Test
    public void updateChannelConfig_whenNewPresencePenalty_thenPresencePenaltyIsUpdated() {

        // Given
        double presencePenalty = 1.3;
        ChannelConfig channelConfig = ChannelConfigFixture.sample().build();

        // When
        channelConfig.updatePresencePenalty(presencePenalty);

        // Then
        assertThat(channelConfig.getModelConfiguration().getPresencePenalty()).isEqualTo(presencePenalty);
    }

    @Test
    public void updateChannelConfig_whenPresencePenaltyGreaterThanAllowed_thenThrowException() {

        // Given
        double presencePenalty = 3;
        ChannelConfig channelConfig = ChannelConfigFixture.sample().build();

        // Then
        assertThrows(BusinessRuleViolationException.class,
                () -> channelConfig.updatePresencePenalty(presencePenalty));
    }

    @Test
    public void updateChannelConfig_whenPresencePenaltyLesserThanAllowed_thenThrowException() {

        // Given
        double presencePenalty = -3;
        ChannelConfig channelConfig = ChannelConfigFixture.sample().build();

        // Then
        assertThrows(BusinessRuleViolationException.class,
                () -> channelConfig.updatePresencePenalty(presencePenalty));
    }

    @Test
    public void updateChannelConfig_whenNewFrequencyPenalty_thenFrequencyPenaltyIsUpdated() {

        // Given
        double frequencyPenalty = 1.3;
        ChannelConfig channelConfig = ChannelConfigFixture.sample().build();

        // When
        channelConfig.updateFrequencyPenalty(frequencyPenalty);

        // Then
        assertThat(channelConfig.getModelConfiguration().getFrequencyPenalty()).isEqualTo(frequencyPenalty);
    }

    @Test
    public void updateChannelConfig_whenFrequencyPenaltyGreaterThanAllowed_thenThrowException() {

        // Given
        double frequencyPenalty = 3;
        ChannelConfig channelConfig = ChannelConfigFixture.sample().build();

        // Then
        assertThrows(BusinessRuleViolationException.class,
                () -> channelConfig.updateFrequencyPenalty(frequencyPenalty));
    }

    @Test
    public void updateChannelConfig_whenPresenceFrequencyLesserThanAllowed_thenThrowException() {

        // Given
        double frequencyPenalty = -3;
        ChannelConfig channelConfig = ChannelConfigFixture.sample().build();

        // Then
        assertThrows(BusinessRuleViolationException.class,
                () -> channelConfig.updateFrequencyPenalty(frequencyPenalty));
    }

    @Test
    public void updateChannelConfig_whenNewLogitBias_thenLogitBiasShouldBeUpdated() {

        // Given
        String token = "TOKEN";
        double bias = 1.3;
        ModelConfiguration modelConfiguration = ModelConfigurationFixture.gpt3516k()
                .logitBias(new HashMap<>())
                .build();

        ChannelConfig channelConfig = ChannelConfigFixture.sample()
                .modelConfiguration(modelConfiguration)
                .build();

        // When
        channelConfig.addLogitBias(token, bias);

        // Then
        assertThat(channelConfig.getModelConfiguration().getLogitBias()).containsEntry(token, bias);
    }

    @Test
    public void updateChannelConfig_whenLogitBiasGreaterThanAllowed_thenThrowException() {

        // Given
        String token = "TOKEN";
        double bias = 200;
        ChannelConfig channelConfig = ChannelConfigFixture.sample().build();

        // Then
        assertThrows(BusinessRuleViolationException.class,
                () -> channelConfig.addLogitBias(token, bias));
    }

    @Test
    public void updateChannelConfig_whenLogitBiasLesserThanAllowed_thenThrowException() {

        // Given
        String token = "TOKEN";
        double bias = -200;
        ChannelConfig channelConfig = ChannelConfigFixture.sample().build();

        // Then
        assertThrows(BusinessRuleViolationException.class,
                () -> channelConfig.addLogitBias(token, bias));
    }

    @Test
    public void updateChannelConfig_whenNewStopSequence_thenStopSequenceShouldBeAdded() {

        // Given
        String token = "TOKEN";
        ModelConfiguration modelConfiguration = ModelConfigurationFixture.gpt3516k()
                .stopSequences(new ArrayList<>())
                .build();

        ChannelConfig channelConfig = ChannelConfigFixture.sample()
                .modelConfiguration(modelConfiguration)
                .build();

        // When
        channelConfig.addStopSequence(token);

        // Then
        assertThat(channelConfig.getModelConfiguration().getStopSequences()).containsExactly(token);
    }

    @Test
    public void updateChannelConfig_whenRemovedStopSequence_thenStopSequenceShouldBeRemoved() {

        // Given
        String token = "TOKEN";
        ModelConfiguration modelConfiguration = ModelConfigurationFixture.gpt3516k()
                .stopSequences(Collections.singletonList(token))
                .build();

        ChannelConfig channelConfig = ChannelConfigFixture.sample()
                .modelConfiguration(modelConfiguration)
                .build();

        // When
        channelConfig.removeStopSequence(token);

        // Then
        assertThat(channelConfig.getModelConfiguration().getStopSequences()).doesNotContain(token);
    }

    @Test
    public void updateChannelConfig_whenRemovedLogitBias_thenLogitBiasShouldBeRemoved() {

        // Given
        String token = "TOKEN";
        double bias = 1.3;
        ModelConfiguration modelConfiguration = ModelConfigurationFixture.gpt3516k()
                .logitBias(Collections.singletonMap(token, bias))
                .build();

        ChannelConfig channelConfig = ChannelConfigFixture.sample()
                .modelConfiguration(modelConfiguration)
                .build();

        // When
        channelConfig.removeLogitBias(token);

        // Then
        assertThat(channelConfig.getModelConfiguration().getLogitBias()).doesNotContainKey(token);
    }

    @Test
    public void createChannelConfig_whenDiscordChannelIdIsNull_thenThrowException() {

        // Given
        ChannelConfig.Builder channelConfigBuilder = ChannelConfigFixture.sample()
                .discordChannelId(null);

        // Then
        assertThrows(BusinessRuleViolationException.class, () -> channelConfigBuilder.build());
    }
}
