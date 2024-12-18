package me.moirai.discordbot.infrastructure.outbound.persistence.adventure;

import me.moirai.discordbot.core.domain.adventure.ContextAttributes;
import me.moirai.discordbot.core.domain.adventure.ContextAttributesFixture;

public class ContextAttributesEntityFixture {

    public static ContextAttributesEntity.Builder sample() {

        ContextAttributes contextAttributes = ContextAttributesFixture.sample().build();
        return ContextAttributesEntity.builder()
                .authorsNote(contextAttributes.getAuthorsNote())
                .nudge(contextAttributes.getNudge())
                .remember(contextAttributes.getRemember())
                .bump(contextAttributes.getBump())
                .bumpFrequency(contextAttributes.getBumpFrequency());
    }
}
