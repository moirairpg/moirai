package es.thalesalv.chatrpg.domain.enums;

import java.util.Arrays;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommandHelpInfo {

    LOREBOOK_LIST("/lb action:list - returns all lore entries.", "lb"),
    LOREBOOK_GET("/lb action:get id:<id> - returns lorebook entry with the ID provided.", "lb"),
    LOREBOOK_CREATE("/lb action:create - opens a window for creating an entry.", "lb"),
    LOREBOOK_DELETE("/lb action:delete id:<id> - deletes the entry with the ID provided.", "lb"),
    LOREBOOK_EDIT("/lb action:edit id:<id> - opens a window for editing the entry with the ID provided.", "lb"),
    EDIT_LAST("/edit - edits the bot's last message.", "edit"),
    EDIT_SPECIFIC("/edit id:<id> - edit bot's message with given ID.", "edit"),
    SET_CHANNEL("/set operation:channel id:<id> - links the channel configuration with given ID to the current channel.", "set"),
    SET_WORLD("/set operation:world id:<id> - links the world with given ID and its lorebook to the current channel's configuration.", "set"),
    UNSET_CHANNEL("/unset operation:channel id:<id> - removes the link between current channel and its current configuration.", "unset"),
    UNSET_WORLD("/unset operation:world id:<id> - removes the link between current channel configuration and its current world.", "unset");

    private final String usageExample;
    private final String commandName;

    public static List<String> findByCommandName(final String name) {

        return Arrays.stream(values())
                .filter(cmd -> cmd.getCommandName().equals(name))
                .map(cmd -> cmd.getUsageExample())
                .toList();
    }
}
