package me.moirai.discordbot.core.application.port;

import java.util.List;
import java.util.Map;

import me.moirai.discordbot.core.domain.channelconfig.ModelConfiguration;
import me.moirai.discordbot.infrastructure.outbound.adapter.response.ChatMessageData;

public interface LorebookEnrichmentPort {

    Map<String, Object> enrichContextWithLorebook(List<ChatMessageData> messagesExtracted, String worldId,
            ModelConfiguration modelConfiguration);
}