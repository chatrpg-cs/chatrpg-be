package es.thalesalv.chatrpg.core.application.service;

import java.util.List;
import java.util.Map;

import es.thalesalv.chatrpg.core.domain.channelconfig.ModelConfiguration;
import reactor.core.publisher.Mono;

public interface ContextSummarizationApplicationService {

    Mono<Map<String, Object>> summarizeWith(String guildId, String authorId, String channelId, String messageId, String botName,
            ModelConfiguration modelConfiguration, List<String> mentionedUserIds);
}
