package es.thalesalv.chatrpg.core.application.usecase.persona;

import es.thalesalv.chatrpg.common.annotation.UseCaseHandler;
import es.thalesalv.chatrpg.common.usecases.AbstractUseCaseHandler;
import es.thalesalv.chatrpg.core.application.usecase.persona.request.CreatePersona;
import es.thalesalv.chatrpg.core.application.usecase.persona.result.CreatePersonaResult;
import es.thalesalv.chatrpg.core.domain.persona.PersonaService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@UseCaseHandler
@RequiredArgsConstructor
public class CreatePersonaHandler extends AbstractUseCaseHandler<CreatePersona, Mono<CreatePersonaResult>> {

    private final PersonaService domainService;

    @Override
    public Mono<CreatePersonaResult> execute(CreatePersona command) {

        return domainService.createFrom(command)
                .map(personaCreated -> CreatePersonaResult.build(personaCreated.getId()));
    }
}
