package es.thalesalv.chatrpg.infrastructure.inbound.api.mapper;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.core.application.command.channelconfig.CreateChannelConfig;
import es.thalesalv.chatrpg.core.application.command.channelconfig.DeleteChannelConfig;
import es.thalesalv.chatrpg.core.application.command.channelconfig.UpdateChannelConfig;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.CreateChannelConfigRequest;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.UpdateChannelConfigRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChannelConfigRequestMapper {

    public CreateChannelConfig toCommand(CreateChannelConfigRequest request, String requesterDiscordId) {

        return CreateChannelConfig.builder()
                .name(request.getName())
                .worldId(request.getWorldId())
                .personaId(request.getPersonaId())
                .visibility(request.getVisibility())
                .aiModel(request.getAiModel())
                .moderation(request.getModeration())
                .maxTokenLimit(request.getMaxTokenLimit())
                .messageHistorySize(request.getMessageHistorySize())
                .temperature(request.getTemperature())
                .frequencyPenalty(request.getFrequencyPenalty())
                .presencePenalty(request.getPresencePenalty())
                .stopSequences(request.getStopSequences())
                .logitBias(request.getLogitBias())
                .writerUsers(request.getWriterUsers())
                .readerUsers(request.getReaderUsers())
                .requesterDiscordId(requesterDiscordId)
                .build();
    }

    public UpdateChannelConfig toCommand(UpdateChannelConfigRequest request, String worldId, String requesterDiscordId) {

        return UpdateChannelConfig.builder()
                .id(worldId)
                .name(request.getName())
                .worldId(request.getWorldId())
                .personaId(request.getPersonaId())
                .visibility(request.getVisibility())
                .aiModel(request.getAiModel())
                .moderation(request.getModeration())
                .maxTokenLimit(request.getMaxTokenLimit())
                .messageHistorySize(request.getMessageHistorySize())
                .temperature(request.getTemperature())
                .frequencyPenalty(request.getFrequencyPenalty())
                .presencePenalty(request.getPresencePenalty())
                .stopSequencesToAdd(request.getStopSequencesToAdd())
                .stopSequencesToRemove(request.getStopSequencesToRemove())
                .logitBiasToAdd(request.getLogitBiasToAdd())
                .logitBiasToRemove(request.getLogitBiasToRemove())
                .writerUsersToAdd(request.getWriterUsersToAdd())
                .writerUsersToRemove(request.getWriterUsersToRemove())
                .readerUsersToAdd(request.getReaderUsersToAdd())
                .readerUsersToRemove(request.getReaderUsersToRemove())
                .requesterDiscordId(requesterDiscordId)
                .build();
    }

    public DeleteChannelConfig toCommand(String worldId, String requesterDiscordId) {

        return DeleteChannelConfig.build(worldId, requesterDiscordId);
    }
}
