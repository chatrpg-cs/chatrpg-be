package es.thalesalv.chatrpg.core.application.query.persona;

import es.thalesalv.chatrpg.common.usecases.UseCase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class GetPersonaById extends UseCase<GetPersonaResult> {

    private final String id;

    public static GetPersonaById build(String id) {

        return new GetPersonaById(id);
    }
}