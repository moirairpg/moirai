package me.moirai.storyengine.core.domain.adventure;

import java.util.UUID;

import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.uuid.Generators;

import me.moirai.storyengine.common.domain.Permission;
import me.moirai.storyengine.common.enums.Moderation;
import me.moirai.storyengine.common.enums.PermissionLevel;
import me.moirai.storyengine.common.enums.Visibility;
import me.moirai.storyengine.core.domain.PermissionFixture;

public class AdventureFixture {

    public static final Long OWNER_ID = 1111L;
    public static final UUID PUBLIC_ID = UUID.fromString("857345aa-2222-0000-0000-000000000000");
    public static final Long NUMERIC_ID = 2L;

    public static Adventure.Builder privateAdventureWithoutNarrator() {

        var builder = baseAdventure();
        builder.visibility(Visibility.fromString("PRIVATE"));

        return builder;
    }

    public static Adventure.Builder privateAdventure() {

        var builder = baseAdventure();
        builder.narrator("Aria", "A helpful guide");
        builder.visibility(Visibility.fromString("PRIVATE"));

        return builder;
    }

    public static Adventure.Builder publicAdventure() {

        var builder = baseAdventure();
        builder.narrator("Aria", "A helpful guide");
        builder.visibility(Visibility.fromString("PUBLIC"));

        return builder;
    }

    public static Adventure.Builder privateAdventureWithUnnamedNarrator() {

        var builder = baseAdventure();
        builder.narrator(null, "A helpful guide");
        builder.visibility(Visibility.fromString("PRIVATE"));

        return builder;
    }

    public static Adventure privateAdventureWithId() {

        var adventure = privateAdventure().build();
        ReflectionTestUtils.setField(adventure, "id", NUMERIC_ID);
        ReflectionTestUtils.setField(adventure, "publicId", PUBLIC_ID);
        return adventure;
    }

    public static Adventure privateAdventureWithUnnamedNarratorWithId() {

        var adventure = privateAdventureWithUnnamedNarrator().build();
        ReflectionTestUtils.setField(adventure, "id", NUMERIC_ID);
        ReflectionTestUtils.setField(adventure, "publicId", PUBLIC_ID);
        return adventure;
    }

    public static Adventure publicAdventureWithId() {

        var adventure = publicAdventure().build();
        ReflectionTestUtils.setField(adventure, "id", NUMERIC_ID);
        ReflectionTestUtils.setField(adventure, "publicId", PUBLIC_ID);
        return adventure;
    }

    public static Adventure publicAdventureWithIdAndPermissions() {

        var adventure = publicAdventureWithId();
        adventure.permissions().addAll(PermissionFixture.samplePermissions());
        return adventure;
    }

    public static Adventure privateAdventureWithIdAndPermissions() {

        var adventure = privateAdventureWithId();
        adventure.permissions().addAll(PermissionFixture.samplePermissions());
        return adventure;
    }

    private static Adventure.Builder baseAdventure() {

        var builder = Adventure.builder();
        builder.name("Name");
        builder.description("This is an RPG world");
        builder.adventureStart("As you enter the city, people around you start looking at you.");
        builder.worldId(Generators.timeBasedEpochGenerator().generate());
        builder.moderation(Moderation.STRICT);
        builder.modelConfiguration(ModelConfigurationFixture.gpt4Mini());
        builder.contextAttributes(ContextAttributesFixture.sample());
        builder.permissions(new Permission(OWNER_ID, PermissionLevel.OWNER));

        return builder;
    }
}
