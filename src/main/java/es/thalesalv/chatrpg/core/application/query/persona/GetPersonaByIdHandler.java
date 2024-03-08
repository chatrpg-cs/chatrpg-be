package es.thalesalv.chatrpg.core.application.query.persona;

import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.common.cqrs.query.QueryHandler;
import es.thalesalv.chatrpg.common.exception.AssetNotFoundException;
import es.thalesalv.chatrpg.core.domain.persona.Persona;
import es.thalesalv.chatrpg.core.domain.persona.PersonaRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GetPersonaByIdHandler extends QueryHandler<GetPersonaById, GetPersonaResult> {

    private final PersonaRepository repository;

    @Override
    public GetPersonaResult handle(GetPersonaById query) {

        Persona persona = repository.findById(query.getId())
                .orElseThrow(() -> new AssetNotFoundException("Persona not found"));

        return mapResult(persona);
    }

    private GetPersonaResult mapResult(Persona persona) {

        return GetPersonaResult.builder()
                .id(persona.getId())
                .build();
    }
}
