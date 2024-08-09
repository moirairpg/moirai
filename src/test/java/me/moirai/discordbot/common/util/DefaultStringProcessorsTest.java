package me.moirai.discordbot.common.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

public class DefaultStringProcessorsTest {

    @Test
    public void processor_whenPrefixWithNameUppercase_thenStripPrefix() {

        // Given
        String name = "John";
        String input = "As John, how are you doing today?";

        // When
        String output = DefaultStringProcessors.stripAsNamePrefixForUppercase(name).apply(input);

        // Then
        assertThat(output).isEqualTo("How are you doing today?");
    }

    @Test
    public void processor_whenPrefixWithNameLowercase_thenStripPrefix() {

        // Given
        String name = "John";
        String input = "as John, how are you doing today?";

        // When
        String output = DefaultStringProcessors.stripAsNamePrefixForLowercase(name).apply(input);

        // Then
        assertThat(output).isEqualTo("how are you doing today?");
    }

    @Test
    public void processor_whenIncompleteSentence_thenStripIncompletePartOut() {

        // Given
        String input = "This is a test. This sentence is complete. But this last one should be cut";

        // When
        String output = DefaultStringProcessors.stripTrailingFragment().apply(input);

        // Then
        assertThat(output).isEqualTo("This is a test. This sentence is complete.");
    }

    @Test
    public void processor_whenChatFormatDetected_thenNormalizeToFlatString() {

        // Given
        String input = "Marcus said: This is a test. This sentence is complete.";

        // When
        String output = DefaultStringProcessors.stripChatPrefix().apply(input);

        // Then
        assertThat(output).isEqualTo("This is a test. This sentence is complete.");
    }

    @Test
    public void processor_whenChatFormatDetectedWithCompositeName_thenNormalizeToFlatString() {

        // Given
        String input = "Marcus John said: This is a test. This sentence is complete.";

        // When
        String output = DefaultStringProcessors.stripChatPrefix().apply(input);

        // Then
        assertThat(output).isEqualTo("This is a test. This sentence is complete.");
    }

    @Test
    public void processor_whenNameToBeReplacedDetected_thenReplaceWithInput() {

        // Given
        String nameToUse = "John";
        String nameToReplace = "Geraldo";
        String input = "as Geraldo, how are you doing today?";

        // When
        String output = DefaultStringProcessors.replaceTemplateWithValue(nameToUse, nameToReplace).apply(input);

        // Then
        assertThat(output).isEqualTo("as John, how are you doing today?");
    }

    @Test
    public void processor_whenNamePlaceholderDetected_thenReplaceWithInput() {

        // Given
        String nameToUse = "John";
        String input = "as {name}, how are you doing today?";

        // When
        String output = DefaultStringProcessors.replacePersonaNamePlaceholderWith(nameToUse).apply(input);

        // Then
        assertThat(output).isEqualTo("as John, how are you doing today?");
    }

    @Test
    public void processor_whenChatMessageFormatRequired_thenFormatInput() {

        // Given
        String nameToUse = "John";
        String input = "how are you doing today?";

        // When
        String output = DefaultStringProcessors.formatChatMessage(nameToUse).apply(input);

        // Then
        assertThat(output).isEqualTo("John said: how are you doing today?");
    }

    @Test
    public void processor_whenMessageWithDiscordMentionsProvided_thenExtractDiscordIds() {

        // Given
        String input = "This is <@4324234>, and <@4234234235> is also here.";

        // When
        List<String> output = DefaultStringProcessors.extractDiscordIds().apply(input);

        // Then
        assertThat(output).isNotNull()
                .isNotEmpty()
                .hasSize(2);

        assertThat(output).element(0).isEqualTo("4324234");
        assertThat(output).element(1).isEqualTo("4234234235");
    }
}
