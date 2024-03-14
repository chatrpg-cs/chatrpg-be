package es.thalesalv.chatrpg.core.application.query.persona;

import java.time.OffsetDateTime;
import java.util.List;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder(builderClassName = "Builder")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class GetPersonaResult {

    private final String id;
    private final String name;
    private final String personality;
    private final String nudgeContent;
    private final String nudgeRole;
    private final String bumpContent;
    private final String bumpRole;
    private final int bumpFrequency;
    private final String visibility;
    private final String ownerDiscordId;
    private final List<String> writerUsers;
    private final List<String> readerUsers;
    private final OffsetDateTime creationDate;
    private final OffsetDateTime lastUpdateDate;
}