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

    @Test
    public void updateModeration() {

        // Given
        Moderation moderation = Moderation.DISABLED;
        ChannelConfig channelConfig = ChannelConfigFixture.sample().build();

        // When
        channelConfig.updateModeration(moderation);

        // Then
        assertThat(channelConfig.getModeration()).isEqualTo(moderation);
    }

    @Test
    public void updateAiModel() {

        // Given
        ArtificialIntelligenceModel aiModel = ArtificialIntelligenceModel.GPT4_128K;
        ChannelConfig channelConfig = ChannelConfigFixture.sample().build();

        // When
        channelConfig.updateAiModel(aiModel);

        // Then
        assertThat(channelConfig.getModelConfiguration().getAiModel()).isEqualTo(aiModel);
    }

    @Test
    public void updateMaxTokenLimit() {

        // Given
        int maxTokenLimit = 100;
        ArtificialIntelligenceModel aiModel = ArtificialIntelligenceModel.GPT35_4K;
        ModelConfiguration modelConfiguration = ModelConfigurationFixture.sample().aiModel(aiModel).build();
        ChannelConfig channelConfig = ChannelConfigFixture.sample().modelConfiguration(modelConfiguration).build();

        // When
        channelConfig.updateMaxTokenLimit(maxTokenLimit);

        // Then
        assertThat(channelConfig.getModelConfiguration().getMaxTokenLimit()).isEqualTo(maxTokenLimit);
    }

    @Test
    public void errorWhenMaxTokenLimitGreaterThanHardLimit() {

        // Given
        int maxTokenLimit = 5000;
        ArtificialIntelligenceModel aiModel = ArtificialIntelligenceModel.GPT35_4K;
        ModelConfiguration modelConfiguration = ModelConfigurationFixture.sample().aiModel(aiModel).build();
        ChannelConfig channelConfig = ChannelConfigFixture.sample().modelConfiguration(modelConfiguration).build();

        // Then
        assertThrows(BusinessRuleViolationException.class,
                () -> channelConfig.updateMaxTokenLimit(maxTokenLimit));
    }

    @Test
    public void errorWhenMaxTokenLimitLowerThanHardLimit() {

        // Given
        int maxTokenLimit = 10;
        ArtificialIntelligenceModel aiModel = ArtificialIntelligenceModel.GPT35_4K;
        ModelConfiguration modelConfiguration = ModelConfigurationFixture.sample().aiModel(aiModel).build();
        ChannelConfig channelConfig = ChannelConfigFixture.sample().modelConfiguration(modelConfiguration).build();

        // Then
        assertThrows(BusinessRuleViolationException.class,
                () -> channelConfig.updateMaxTokenLimit(maxTokenLimit));
    }

    @Test
    public void updateMessageHistorySize() {

        // Given
        int messageHistorySize = 100;
        ArtificialIntelligenceModel aiModel = ArtificialIntelligenceModel.GPT35_4K;
        ModelConfiguration modelConfiguration = ModelConfigurationFixture.sample().aiModel(aiModel).build();
        ChannelConfig channelConfig = ChannelConfigFixture.sample().modelConfiguration(modelConfiguration).build();

        // When
        channelConfig.updateMessageHistorySize(messageHistorySize);

        // Then
        assertThat(channelConfig.getModelConfiguration().getMessageHistorySize()).isEqualTo(messageHistorySize);
    }

    @Test
    public void errorWhenMessageHistorySizeGreaterThanHardLimit() {

        // Given
        int messageHistorySize = 5000;
        ArtificialIntelligenceModel aiModel = ArtificialIntelligenceModel.GPT35_4K;
        ModelConfiguration modelConfiguration = ModelConfigurationFixture.sample().aiModel(aiModel).build();
        ChannelConfig channelConfig = ChannelConfigFixture.sample().modelConfiguration(modelConfiguration).build();

        // Then
        assertThrows(BusinessRuleViolationException.class,
                () -> channelConfig.updateMessageHistorySize(messageHistorySize));
    }

    @Test
    public void errorWhenMessageHistorySizeLowerThanHardLimit() {

        // Given
        int messageHistorySize = 5;
        ArtificialIntelligenceModel aiModel = ArtificialIntelligenceModel.GPT35_4K;
        ModelConfiguration modelConfiguration = ModelConfigurationFixture.sample().aiModel(aiModel).build();
        ChannelConfig channelConfig = ChannelConfigFixture.sample().modelConfiguration(modelConfiguration).build();

        // Then
        assertThrows(BusinessRuleViolationException.class,
                () -> channelConfig.updateMessageHistorySize(messageHistorySize));
    }

    @Test
    public void updateTemperature() {

        // Given
        double temperature = 1.3;
        ChannelConfig channelConfig = ChannelConfigFixture.sample().build();

        // When
        channelConfig.updateTemperature(temperature);

        // Then
        assertThat(channelConfig.getModelConfiguration().getTemperature()).isEqualTo(temperature);
    }

    @Test
    public void errorWhenTemperatureHigherThanLimit() {

        // Given
        double temperature = 3;
        ChannelConfig channelConfig = ChannelConfigFixture.sample().build();

        // Then
        assertThrows(BusinessRuleViolationException.class,
                () -> channelConfig.updateTemperature(temperature));
    }

    @Test
    public void errorWhenTemperatureLowerThanLimit() {

        // Given
        double temperature = 0;
        ChannelConfig channelConfig = ChannelConfigFixture.sample().build();

        // Then
        assertThrows(BusinessRuleViolationException.class,
                () -> channelConfig.updateTemperature(temperature));
    }

    @Test
    public void updatePresencePenalty() {

        // Given
        double presencePenalty = 1.3;
        ChannelConfig channelConfig = ChannelConfigFixture.sample().build();

        // When
        channelConfig.updatePresencePenalty(presencePenalty);

        // Then
        assertThat(channelConfig.getModelConfiguration().getPresencePenalty()).isEqualTo(presencePenalty);
    }

    @Test
    public void errorWhenPresencePenaltyHigherThanLimit() {

        // Given
        double presencePenalty = 3;
        ChannelConfig channelConfig = ChannelConfigFixture.sample().build();

        // Then
        assertThrows(BusinessRuleViolationException.class,
                () -> channelConfig.updatePresencePenalty(presencePenalty));
    }

    @Test
    public void errorWhenPresencePenaltyLowerThanLimit() {

        // Given
        double presencePenalty = -3;
        ChannelConfig channelConfig = ChannelConfigFixture.sample().build();

        // Then
        assertThrows(BusinessRuleViolationException.class,
                () -> channelConfig.updatePresencePenalty(presencePenalty));
    }

    @Test
    public void updateFrequencyPenalty() {

        // Given
        double frequencyPenalty = 1.3;
        ChannelConfig channelConfig = ChannelConfigFixture.sample().build();

        // When
        channelConfig.updateFrequencyPenalty(frequencyPenalty);

        // Then
        assertThat(channelConfig.getModelConfiguration().getFrequencyPenalty()).isEqualTo(frequencyPenalty);
    }

    @Test
    public void errorWhenFrequencyPenaltyHigherThanLimit() {

        // Given
        double frequencyPenalty = 3;
        ChannelConfig channelConfig = ChannelConfigFixture.sample().build();

        // Then
        assertThrows(BusinessRuleViolationException.class,
                () -> channelConfig.updateFrequencyPenalty(frequencyPenalty));
    }

    @Test
    public void errorWhenFrequencyPenaltyLowerThanLimit() {

        // Given
        double frequencyPenalty = -3;
        ChannelConfig channelConfig = ChannelConfigFixture.sample().build();

        // Then
        assertThrows(BusinessRuleViolationException.class,
                () -> channelConfig.updateFrequencyPenalty(frequencyPenalty));
    }

    @Test
    public void updateLogitBias() {

        // Given
        String token = "TOKEN";
        double bias = 1.3;
        ModelConfiguration modelConfiguration = ModelConfigurationFixture.sample()
                .logitBias(new HashMap<>())
                .build();

        ChannelConfig channelConfig = ChannelConfigFixture.sample()
                .modelConfiguration(modelConfiguration)
                .build();

        // When
        channelConfig.addLogitBias(token, bias);

        // Then
        assertThat(channelConfig.getModelConfiguration().getLogitBias().get(token)).isEqualTo(bias);
    }

    @Test
    public void errorWhenLogitBiasHigherThanLimit() {

        // Given
        String token = "TOKEN";
        double bias = 200;
        ChannelConfig channelConfig = ChannelConfigFixture.sample().build();

        // Then
        assertThrows(BusinessRuleViolationException.class,
                () -> channelConfig.addLogitBias(token, bias));
    }

    @Test
    public void errorWhenLogitBiasLowerThanLimit() {

        // Given
        String token = "TOKEN";
        double bias = -200;
        ChannelConfig channelConfig = ChannelConfigFixture.sample().build();

        // Then
        assertThrows(BusinessRuleViolationException.class,
                () -> channelConfig.addLogitBias(token, bias));
    }

    @Test
    public void addStopSequence() {

        // Given
        String token = "TOKEN";
        ModelConfiguration modelConfiguration = ModelConfigurationFixture.sample()
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
    public void removeStopSequence() {

        // Given
        String token = "TOKEN";
        ModelConfiguration modelConfiguration = ModelConfigurationFixture.sample()
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
    public void removeLogitBias() {

        // Given
        String token = "TOKEN";
        double bias = 1.3;
        ModelConfiguration modelConfiguration = ModelConfigurationFixture.sample()
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
}
