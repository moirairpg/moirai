package me.moirai.storyengine.core.port.inbound;

import java.util.List;
import java.util.Set;

import me.moirai.storyengine.core.domain.adventure.AdventureFixture;
import me.moirai.storyengine.core.domain.world.WorldFixture;
import me.moirai.storyengine.core.port.inbound.adventure.AdventureDetails;
import me.moirai.storyengine.core.port.inbound.adventure.ContextAttributesDto;
import me.moirai.storyengine.core.port.inbound.adventure.ModelConfigurationDto;

public class GetAdventureResultFixture {

    public static AdventureDetails privateAdventure() {

        var adventure = AdventureFixture.privateAdventureWithIdAndPermissions();

        var modelConfiguration = new ModelConfigurationDto(
                adventure.getModelConfiguration().getAiModel(),
                adventure.getModelConfiguration().getMaxTokenLimit(),
                adventure.getModelConfiguration().getTemperature());

        var contextAttributes = new ContextAttributesDto(
                adventure.getContextAttributes().nudge(),
                adventure.getContextAttributes().authorsNote(),
                adventure.getContextAttributes().scene(),
                adventure.getContextAttributes().bump(),
                adventure.getContextAttributes().bumpFrequency());

        return new AdventureDetails(
                adventure.getPublicId(),
                adventure.getName(),
                adventure.getDescription(),
                adventure.getAdventureStart(),
                WorldFixture.PUBLIC_ID,
                adventure.getNarratorName(),
                adventure.getNarratorPersonality(),
                adventure.getVisibility(),
                adventure.getModeration(),
                null,
                adventure.getCreationDate(),
                adventure.getLastUpdateDate(),
                modelConfiguration,
                contextAttributes,
                true,
                true,
                Set.of(),
                List.of(),
                null,
                null);
    }

    public static AdventureDetails publicAdventure() {

        var adventure = AdventureFixture.publicAdventureWithIdAndPermissions();

        var modelConfiguration = new ModelConfigurationDto(
                adventure.getModelConfiguration().getAiModel(),
                adventure.getModelConfiguration().getMaxTokenLimit(),
                adventure.getModelConfiguration().getTemperature());

        var contextAttributes = new ContextAttributesDto(
                adventure.getContextAttributes().nudge(),
                adventure.getContextAttributes().authorsNote(),
                adventure.getContextAttributes().scene(),
                adventure.getContextAttributes().bump(),
                adventure.getContextAttributes().bumpFrequency());

        return new AdventureDetails(
                adventure.getPublicId(),
                adventure.getName(),
                adventure.getDescription(),
                adventure.getAdventureStart(),
                WorldFixture.PUBLIC_ID,
                adventure.getNarratorName(),
                adventure.getNarratorPersonality(),
                adventure.getVisibility(),
                adventure.getModeration(),
                null,
                adventure.getCreationDate(),
                adventure.getLastUpdateDate(),
                modelConfiguration,
                contextAttributes,
                true,
                true,
                Set.of(),
                List.of(),
                null,
                null);
    }
}
