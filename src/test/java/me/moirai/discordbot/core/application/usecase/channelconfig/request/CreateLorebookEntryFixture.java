package me.moirai.discordbot.core.application.usecase.channelconfig.request;

import me.moirai.discordbot.core.application.usecase.world.request.CreateWorldLorebookEntry;

public class CreateLorebookEntryFixture {

    public static CreateWorldLorebookEntry.Builder sampleLorebookEntry() {

        CreateWorldLorebookEntry.Builder builder = CreateWorldLorebookEntry.builder();
        builder.name("White River");
        builder.description("The White River goes through Falkreath, Whiterun and ends in Eastmarch.");
        builder.regex("[Ww]hite [Rr]iver");

        return builder;
    }

    public static CreateWorldLorebookEntry.Builder samplePlayerCharacterLorebookEntry() {

        CreateWorldLorebookEntry.Builder builder = CreateWorldLorebookEntry.builder();
        builder.name("Volin Habar");
        builder.description("Volin Habar is a warrior that fights with a sword.");
        builder.regex("[Vv]olin [Hh]abar|[Vv]oha");
        builder.playerDiscordId("2423423423423");

        return builder;
    }
}
