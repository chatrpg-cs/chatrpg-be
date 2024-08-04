package me.moirai.discordbot.core.application.usecase.discord.contextmenu;

import me.moirai.discordbot.common.usecases.UseCase;

public final class EditMessage extends UseCase<Void> {

    private final String channelId;
    private final String messageId;
    private final String messageContent;

    private EditMessage(String channelId, String messageId, String messageContent) {
        this.channelId = channelId;
        this.messageId = messageId;
        this.messageContent = messageContent;
    }

    public static EditMessage build(String channelId, String messageId, String messageContent) {
        return new EditMessage(channelId, messageId, messageContent);
    }

    public String getChannelId() {
        return channelId;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getMessageContent() {
        return messageContent;
    }
}
