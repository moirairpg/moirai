package me.moirai.storyengine.core.domain.adventure;

import static me.moirai.storyengine.common.util.DefaultStringProcessors.formatScene;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record ContextAttributes(
        @Column(name = "nudge") String nudge,
        @Column(name = "authors_note") String authorsNote,
        @Column(name = "scene") String scene,
        @Column(name = "bump") String bump,
        @Column(name = "bump_frequency") Integer bumpFrequency) {

    public ContextAttributes updateNudge(String nudge) {

        return new ContextAttributes(nudge, authorsNote, scene, bump, bumpFrequency);
    }

    public ContextAttributes updateBump(String bump) {

        return new ContextAttributes(nudge, authorsNote, scene, bump, bumpFrequency);
    }

    public ContextAttributes updateBumpFrequency(int bumpFrequency) {

        return new ContextAttributes(nudge, authorsNote, scene, bump, bumpFrequency);
    }

    public ContextAttributes updateAuthorsNote(String authorsNote) {

        return new ContextAttributes(nudge, authorsNote, scene, bump, bumpFrequency);
    }

    public ContextAttributes updateScene(String scene) {

        return new ContextAttributes(nudge, authorsNote, scene, bump, bumpFrequency);
    }

    public List<String> asText() {

        var text = new ArrayList<String>();

        if (isNotBlank(authorsNote)) {
            text.add(authorsNote);
        }

        if (isNotBlank(scene)) {
            text.add(formatScene().apply(scene));
        }

        if (isNotBlank(nudge)) {
            text.add(nudge);
        }

        return Collections.unmodifiableList(text);
    }
}
