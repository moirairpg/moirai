package me.moirai.storyengine.core.port.inbound;

import java.util.List;

import me.moirai.storyengine.common.enums.ArtificialIntelligenceModel;
import me.moirai.storyengine.core.domain.adventure.Adventure;
import me.moirai.storyengine.core.domain.adventure.AdventureFixture;
import me.moirai.storyengine.core.domain.userdetails.UserFixture;
import me.moirai.storyengine.core.port.inbound.adventure.ContextAttributesDto;
import me.moirai.storyengine.core.port.inbound.adventure.ModelConfigurationDto;
import me.moirai.storyengine.core.port.inbound.adventure.UpdateAdventure;

public class UpdateAdventureFixture {

    public static UpdateAdventure sample() {

        var adventure = AdventureFixture.privateAdventure().build();
        return new UpdateAdventure(
                AdventureFixture.PUBLIC_ID,
                adventure.getName(),
                adventure.getDescription(),
                adventure.getAdventureStart(),
                "Aria",
                "A helpful guide",
                adventure.getModeration(),
                null,
                null,
                new ModelConfigurationDto(
                        adventure.getModelConfiguration().getAiModel(),
                        adventure.getModelConfiguration().getMaxTokenLimit(),
                        adventure.getModelConfiguration().getTemperature()),
                new ContextAttributesDto(
                        adventure.getContextAttributes().nudge(),
                        adventure.getContextAttributes().authorsNote(),
                        adventure.getContextAttributes().scene(),
                        adventure.getContextAttributes().bump(),
                        adventure.getContextAttributes().bumpFrequency()),
                List.of(),
                List.of(),
                List.of(),
                UserFixture.PUBLIC_ID);
    }

    public static UpdateAdventure sampleWithRequesterId(String requesterId) {

        Adventure adventure = AdventureFixture.privateAdventure().build();
        return new UpdateAdventure(
                AdventureFixture.PUBLIC_ID,
                adventure.getName(),
                adventure.getDescription(),
                adventure.getAdventureStart(),
                "Aria",
                "A helpful guide",
                adventure.getModeration(),
                null,
                null,
                new ModelConfigurationDto(
                        adventure.getModelConfiguration().getAiModel(),
                        adventure.getModelConfiguration().getMaxTokenLimit(),
                        adventure.getModelConfiguration().getTemperature()),
                new ContextAttributesDto(
                        adventure.getContextAttributes().nudge(),
                        adventure.getContextAttributes().authorsNote(),
                        adventure.getContextAttributes().scene(),
                        adventure.getContextAttributes().bump(),
                        adventure.getContextAttributes().bumpFrequency()),
                List.of(),
                List.of(),
                List.of(),
                UserFixture.PUBLIC_ID);
    }

    public static UpdateAdventure sampleWithModelConfiguration(
            ArtificialIntelligenceModel aiModel,
            Integer maxTokenLimit,
            Double temperature) {

        Adventure adventure = AdventureFixture.privateAdventure().build();
        return new UpdateAdventure(
                AdventureFixture.PUBLIC_ID,
                adventure.getName(),
                adventure.getDescription(),
                adventure.getAdventureStart(),
                "Aria",
                "A helpful guide",
                adventure.getModeration(),
                null,
                null,
                new ModelConfigurationDto(aiModel, maxTokenLimit, temperature),
                new ContextAttributesDto(
                        adventure.getContextAttributes().nudge(),
                        adventure.getContextAttributes().authorsNote(),
                        adventure.getContextAttributes().scene(),
                        adventure.getContextAttributes().bump(),
                        adventure.getContextAttributes().bumpFrequency()),
                List.of(),
                List.of(),
                List.of(),
                UserFixture.PUBLIC_ID);
    }

}
