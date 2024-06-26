package es.thalesalv.chatrpg.infrastructure.inbound.api.request;

import es.thalesalv.chatrpg.core.domain.persona.Persona;
import es.thalesalv.chatrpg.core.domain.persona.PersonaFixture;

public class UpdatePersonaRequestFixture {

    public static UpdatePersonaRequest.Builder privatePersona() {

        Persona persona = PersonaFixture.privatePersona().build();
        return UpdatePersonaRequest.builder()
                .name(persona.getName())
                .personality(persona.getPersonality())
                .visibility(persona.getVisibility().toString())
                .gameMode(persona.getGameMode().name())
                .usersAllowedToReadToAdd(persona.getUsersAllowedToRead())
                .usersAllowedToWriteToAdd(persona.getUsersAllowedToWrite())
                .nudgeContent(persona.getNudge().getContent())
                .nudgeRole(persona.getNudge().getRole().toString())
                .bumpContent(persona.getBump().getContent())
                .bumpRole(persona.getBump().getRole().toString())
                .bumpFrequency(persona.getBump().getFrequency());
    }
}
