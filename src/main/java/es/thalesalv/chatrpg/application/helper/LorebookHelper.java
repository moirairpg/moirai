package es.thalesalv.chatrpg.application.helper;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.domain.model.bot.LorebookEntry;
import es.thalesalv.chatrpg.domain.model.bot.World;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.User;

@Component
public class LorebookHelper {

    private static final String TAG_EXPRESSION = "(@|)";
    private static final String CHARACTER_DESCRIPTION = "{0} description: {1}";
    private static final String RPG_DM_INSTRUCTIONS = "I will remember to never act or speak on behalf of {0}. I will not repeat what {0} just said. I will only describe the world around {0}.";

    private static final Logger LOGGER = LoggerFactory.getLogger(LorebookHelper.class);

    public List<String> rpgModeLorebookEntries(final List<LorebookEntry> entriesFound, final List<String> messages,
            final JDA jda) {

        LOGGER.debug("Entered processEntriesFoundForRpg");
        entriesFound.forEach(entry -> {
            if (StringUtils.isNotBlank(entry.getPlayerDiscordId())) {
                messages.add(0, MessageFormat.format(RPG_DM_INSTRUCTIONS, entry.getName()));
            }

            messages.add(0, MessageFormat.format(CHARACTER_DESCRIPTION, entry.getName(), entry.getDescription()));
            Optional.ofNullable(entry.getPlayerDiscordId())
                    .ifPresent(id -> {
                        final User p = jda.retrieveUserById(id)
                                .complete();

                        messages.replaceAll(m -> m.replaceAll(p.getAsTag(), entry.getName())
                                .replaceAll(TAG_EXPRESSION + p.getName(), entry.getName()));
                    });
        });

        return messages;
    }

    public List<String> chatModeLorebookEntries(final List<LorebookEntry> entriesFound, final List<String> messages) {

        LOGGER.debug("Entered processEntriesFoundForChat");
        entriesFound.forEach(entry -> messages.add(0,
                MessageFormat.format(CHARACTER_DESCRIPTION, entry.getName(), entry.getDescription())));

        return messages;
    }

    /**
     * Extracts player characters entries from database given the player's Discord
     * user ID
     *
     * @param entriesFound List of entries found in the messages until now
     * @param messages     List of messages in the channel
     * @param player       Player user
     * @param mentions     Mentioned users (their characters are extracted too)
     * @param world        World containing the lore entries that should be used for
     *                     matching
     */
    public List<String> handlePlayerCharacterEntries(final List<LorebookEntry> entriesFound,
            final List<String> messages, final User player, final Mentions mentions, final World world) {

        LOGGER.debug("Entered player character entry handling");
        final List<LorebookEntry> entries = world.getLorebook();
        entries.stream()
                .filter(entry -> StringUtils.isNotBlank(entry.getPlayerDiscordId()))
                .filter(entry -> entry.getPlayerDiscordId()
                        .equals(player.getId()))
                .findFirst()
                .ifPresent(entry -> {
                    entriesFound.add(entry);
                    messages.replaceAll(m -> m.replaceAll(player.getAsTag(), entry.getName())
                            .replaceAll(TAG_EXPRESSION + player.getName(), entry.getName()));
                });

        mentions.getUsers()
                .forEach(mention -> entries.stream()
                        .filter(entry -> StringUtils.isNotBlank(entry.getPlayerDiscordId()))
                        .filter(entry -> entry.getPlayerDiscordId()
                                .equals(mention.getId()))
                        .findFirst()
                        .ifPresent(entry -> {
                            entriesFound.add(entry);
                            messages.replaceAll(m -> m.replaceAll(mention.getAsTag(), entry.getName())
                                    .replaceAll(TAG_EXPRESSION + mention.getName(), entry.getName()));
                        }));

        return messages;
    }

    /**
     * Extracts lore entries from the conversation when they're mentioned by name
     *
     * @param messageList List of messages in the channel
     * @param world       World containing the lore entries that should be used for
     *                    matching
     * @return Set containing all entries found
     */
    public List<LorebookEntry> handleEntriesMentioned(final List<String> messageList, final World world) {

        LOGGER.debug("Entered mentioned entries handling");
        final String messages = String.join("\n", messageList);
        return world.getLorebook()
                .stream()
                .map(entry -> {
                    Pattern p = Pattern.compile(entry.getRegex());
                    Matcher matcher = p.matcher(messages);
                    if (matcher.find()) {
                        return entry;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
