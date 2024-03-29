package es.thalesalv.chatrpg.domain.enums;

import java.util.Arrays;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommandHelpInfo {

    LOREBOOK_LIST("/lb action:list - returns all lore entries.", "lb"),
    LOREBOOK_GET("/lb action:get - returns lorebook attached to the current channel.", "lb"),
    LOREBOOK_GET_ID("/lb action:get id:<id> - returns lorebook entry with the ID provided.", "lb"),
    LOREBOOK_CREATE("/lb action:create - opens a window for creating an entry.", "lb"),
    LOREBOOK_DELETE("/lb action:delete id:<id> - deletes the entry with the ID provided.", "lb"),
    LOREBOOK_EDIT("/lb action:edit id:<id> - opens a window for editing the entry with the ID provided.", "lb"),
    EDIT_LAST("/edit - edits the bot's last message.", "edit"),
    EDIT_SPECIFIC("/edit id:<id> - edit bot's message with given ID.", "edit"),
    SET_CHANNEL(
            "/set operation:channel id:<id> - links the channel configuration with given ID to the current channel.",
            "set"),
    SET_WORLD(
            "/set operation:world id:<id> - links the world with given ID and its lorebook to the current channel's configuration.",
            "set"),
    UNSET_CHANNEL(
            "/unset operation:channel id:<id> - removes the link between current channel and its current configuration.",
            "unset"),
    UNSET_WORLD(
            "/unset operation:world id:<id> - removes the link between current channel configuration and its current world.",
            "unset"),
    WORLD_GET(
            "/wd action:get - retrieves the world attached to the current channel and its data if it's public or owned by the user.",
            "wd"),
    WORLD_LIST(
            "/wd action:list - retrieves all worlds that are public or owned by the user issuing the command.",
            "wd"),
    CHCONF_GET(
            "/chconf action:get - retrieves the config attached to the current channel and its data if it's public or owned by the user.",
            "chconf"),
    CHCONF_LIST(
            "/chconf action:list - retrieves all configs that are public or owned by the user issuing the command.",
            "chconf");

    private final String usageExample;
    private final String commandName;

    public static List<String> findByCommandName(final String name) {

        return Arrays.stream(values())
                .filter(cmd -> cmd.getCommandName()
                        .equals(name))
                .map(CommandHelpInfo::getUsageExample)
                .toList();
    }
}
