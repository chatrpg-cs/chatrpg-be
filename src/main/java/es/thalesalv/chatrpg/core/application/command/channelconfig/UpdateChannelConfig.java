package es.thalesalv.chatrpg.core.application.command.channelconfig;

import java.util.List;
import java.util.Map;

import es.thalesalv.chatrpg.common.usecases.UseCase;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(builderClassName = "Builder")
public final class UpdateChannelConfig extends UseCase<UpdateChannelConfigResult> {

    private final String id;
    private final String name;
    private final String worldId;
    private final String personaId;
    private final String visibility;
    private final String aiModel;
    private final String moderation;
    private final Integer maxTokenLimit;
    private final Integer messageHistorySize;
    private final Double temperature;
    private final Double frequencyPenalty;
    private final Double presencePenalty;
    private final List<String> stopSequencesToAdd;
    private final List<String> stopSequencesToRemove;
    private final Map<String, Double> logitBiasToAdd;
    private final List<String> logitBiasToRemove;
    private final List<String> writerUsersToAdd;
    private final List<String> writerUsersToRemove;
    private final List<String> readerUsersToAdd;
    private final List<String> readerUsersToRemove;
    private final String requesterDiscordId;
}
