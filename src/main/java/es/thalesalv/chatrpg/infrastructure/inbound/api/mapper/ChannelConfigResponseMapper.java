package es.thalesalv.chatrpg.infrastructure.inbound.api.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.core.application.command.channelconfig.CreateChannelConfigResult;
import es.thalesalv.chatrpg.core.application.command.channelconfig.UpdateChannelConfigResult;
import es.thalesalv.chatrpg.core.application.query.channelconfig.GetChannelConfigResult;
import es.thalesalv.chatrpg.core.application.query.channelconfig.SearchChannelConfigsResult;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.ChannelConfigResponse;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.CreateChannelConfigResponse;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.SearchChannelConfigsResponse;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.UpdateChannelConfigResponse;

@Component
public class ChannelConfigResponseMapper {

    public SearchChannelConfigsResponse toResponse(SearchChannelConfigsResult result) {

        List<ChannelConfigResponse> worlds = result.getResults()
                .stream()
                .map(this::toResponse)
                .toList();

        return SearchChannelConfigsResponse.builder()
                .page(result.getPage())
                .resultsInPage(result.getItems())
                .totalPages(result.getTotalPages())
                .totalResults(result.getTotalItems())
                .results(worlds)
                .build();
    }

    public ChannelConfigResponse toResponse(GetChannelConfigResult result) {

        return ChannelConfigResponse.builder()
                .id(result.getId())
                .name(result.getName())
                .worldId(result.getWorldId())
                .personaId(result.getPersonaId())
                .visibility(result.getVisibility())
                .aiModel(result.getAiModel())
                .moderation(result.getModeration())
                .maxTokenLimit(result.getMaxTokenLimit())
                .messageHistorySize(result.getMessageHistorySize())
                .temperature(result.getTemperature())
                .frequencyPenalty(result.getFrequencyPenalty())
                .presencePenalty(result.getPresencePenalty())
                .stopSequences(result.getStopSequences())
                .logitBias(result.getLogitBias())
                .usersAllowedToWrite(result.getUsersAllowedToWrite())
                .usersAllowedToRead(result.getUsersAllowedToRead())
                .build();
    }

    public CreateChannelConfigResponse toResponse(CreateChannelConfigResult result) {

        return CreateChannelConfigResponse.build(result.getId());
    }

    public UpdateChannelConfigResponse toResponse(UpdateChannelConfigResult result) {

        return UpdateChannelConfigResponse.build(result.getLastUpdatedDateTime());
    }
}
