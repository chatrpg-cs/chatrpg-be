package es.thalesalv.chatrpg.core.application.query.persona;

import java.util.List;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder(builderClassName = "Builder")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SearchPersonasResult {

    private final Integer page;
    private final Integer results;
    private final List<GetPersonaResult> personas;
}
