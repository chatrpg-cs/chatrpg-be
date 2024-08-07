package me.moirai.discordbot.core.application.usecase.persona;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.discordbot.core.application.usecase.persona.request.UpdatePersona;
import me.moirai.discordbot.core.domain.persona.Persona;
import me.moirai.discordbot.core.domain.persona.PersonaFixture;
import me.moirai.discordbot.core.domain.persona.PersonaServiceImpl;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class UpdatePersonaHandlerTest {

    @Mock
    private PersonaServiceImpl service;

    @InjectMocks
    private UpdatePersonaHandler handler;

    @Test
    public void updatePersona() {

        // Given
        String id = "PRSNID";

        UpdatePersona command = UpdatePersona.builder()
                .id(id)
                .name("MoirAI")
                .personality("I am a Discord chatbot")
                .visibility("PUBLIC")
                .requesterDiscordId("CRTID")
                .build();

        Persona expectedUpdatedPersona = PersonaFixture.privatePersona().build();

        when(service.update(any(UpdatePersona.class)))
                .thenReturn(Mono.just(expectedUpdatedPersona));

        // Then
        StepVerifier.create(handler.handle(command))
                .assertNext(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result.getLastUpdatedDateTime()).isEqualTo(expectedUpdatedPersona.getLastUpdateDate());
                })
                .verifyComplete();
    }
}
