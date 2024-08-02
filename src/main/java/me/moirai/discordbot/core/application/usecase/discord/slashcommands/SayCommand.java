package me.moirai.discordbot.core.application.usecase.discord.slashcommands;

import me.moirai.discordbot.common.usecases.UseCase;

public class SayCommand extends UseCase<Void> {

    private final String channelId;
    private final String messageContent;

    private SayCommand(String channelId, String messageContent) {

        this.channelId = channelId;
        this.messageContent = messageContent;
    }

    public static SayCommand build(String channelId, String messageContent) {
        return new SayCommand(channelId, messageContent);
    }

    public String getMessageContent() {
        return messageContent;
    }

    public String getChannelId() {
        return channelId;
    }
}