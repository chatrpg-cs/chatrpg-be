package es.thalesalv.chatrpg.infrastructure.inbound.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import es.thalesalv.chatrpg.common.usecases.UseCaseRunner;
import es.thalesalv.chatrpg.common.web.SecurityContextAware;
import es.thalesalv.chatrpg.core.application.usecase.persona.request.CreatePersona;
import es.thalesalv.chatrpg.core.application.usecase.persona.request.DeletePersona;
import es.thalesalv.chatrpg.core.application.usecase.persona.request.GetPersonaById;
import es.thalesalv.chatrpg.core.application.usecase.persona.request.SearchPersonasWithReadAccess;
import es.thalesalv.chatrpg.core.application.usecase.persona.request.SearchPersonasWithWriteAccess;
import es.thalesalv.chatrpg.core.application.usecase.persona.request.UpdatePersona;
import es.thalesalv.chatrpg.infrastructure.inbound.api.mapper.PersonaRequestMapper;
import es.thalesalv.chatrpg.infrastructure.inbound.api.mapper.PersonaResponseMapper;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.CreatePersonaRequest;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.PersonaSearchParameters;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.UpdatePersonaRequest;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.CreatePersonaResponse;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.PersonaResponse;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.SearchPersonasResponse;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.UpdatePersonaResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/persona")
@RequiredArgsConstructor
@Tag(name = "Personas", description = "Endpoints for managing ChatRPG Personas")
public class PersonaController extends SecurityContextAware {

    private final UseCaseRunner useCaseRunner;
    private final PersonaRequestMapper requestMapper;
    private final PersonaResponseMapper responseMapper;

    @GetMapping("/search")
    @ResponseStatus(code = HttpStatus.OK)
    public Mono<SearchPersonasResponse> searchPersonaWithReadAccess(PersonaSearchParameters searchParameters) {

        return mapWithAuthenticatedUser(authenticatedUser -> {

            SearchPersonasWithReadAccess query = SearchPersonasWithReadAccess.builder()
                    .page(searchParameters.getPage())
                    .items(searchParameters.getItems())
                    .sortByField(searchParameters.getSortByField())
                    .direction(searchParameters.getDirection())
                    .name(searchParameters.getName())
                    .requesterDiscordId(authenticatedUser.getId())
                    .build();

            return responseMapper.toResponse(useCaseRunner.run(query));
        });
    }

    @GetMapping("/search/own")
    @ResponseStatus(code = HttpStatus.OK)
    public Mono<SearchPersonasResponse> searchPersonaWithWriteAccess(PersonaSearchParameters searchParameters) {

        return mapWithAuthenticatedUser(authenticatedUser -> {

            SearchPersonasWithWriteAccess query = SearchPersonasWithWriteAccess.builder()
                    .page(searchParameters.getPage())
                    .items(searchParameters.getItems())
                    .sortByField(searchParameters.getSortByField())
                    .direction(searchParameters.getDirection())
                    .name(searchParameters.getName())
                    .requesterDiscordId(authenticatedUser.getId())
                    .build();

            return responseMapper.toResponse(useCaseRunner.run(query));
        });
    }

    @GetMapping("/{personaId}")
    @ResponseStatus(code = HttpStatus.OK)
    public Mono<PersonaResponse> getPersonaById(@PathVariable(name = "personaId", required = true) String personaId) {

        return mapWithAuthenticatedUser(authenticatedUser -> {

            GetPersonaById query = GetPersonaById.build(personaId, authenticatedUser.getId());
            return responseMapper.toResponse(useCaseRunner.run(query));
        });
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public Mono<CreatePersonaResponse> createPersona(@Valid @RequestBody CreatePersonaRequest request) {

        return flatMapWithAuthenticatedUser(authenticatedUser -> {

            CreatePersona command = requestMapper.toCommand(request, authenticatedUser.getId());
            return useCaseRunner.run(command);
        })
                .map(result -> responseMapper.toResponse(result));
    }

    @PutMapping("/{personaId}")
    @ResponseStatus(code = HttpStatus.OK)
    public Mono<UpdatePersonaResponse> updatePersona(
            @PathVariable(name = "personaId", required = true) String personaId,
            @Valid @RequestBody UpdatePersonaRequest request) {

        return flatMapWithAuthenticatedUser(authenticatedUser -> {

            UpdatePersona command = requestMapper.toCommand(request, personaId,
                    authenticatedUser.getId());

            return useCaseRunner.run(command);
        })
                .map(result -> responseMapper.toResponse(result));
    }

    @DeleteMapping("/{personaId}")
    @ResponseStatus(code = HttpStatus.OK)
    public Mono<Void> deletePersona(@PathVariable(name = "personaId", required = true) String personaId) {

        return flatMapWithAuthenticatedUser(authenticatedUser -> {

            DeletePersona command = requestMapper.toCommand(personaId, authenticatedUser.getId());
            useCaseRunner.run(command);

            return Mono.empty();
        });
    }
}
