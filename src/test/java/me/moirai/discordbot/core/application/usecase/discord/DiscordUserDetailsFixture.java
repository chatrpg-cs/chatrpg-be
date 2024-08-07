package me.moirai.discordbot.core.application.usecase.discord;

public class DiscordUserDetailsFixture {

    private static final String MENTION_BASE = "<@%s>";

    public static DiscordUserDetails.Builder create() {

        String userId = "123456";
        return DiscordUserDetails.builder()
                .id(userId)
                .mention(String.format(MENTION_BASE, userId))
                .nickname("natalis")
                .username("john.natalis");
    }
}
