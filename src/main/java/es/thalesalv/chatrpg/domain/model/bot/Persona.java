package es.thalesalv.chatrpg.domain.model.bot;

import java.util.List;

import es.thalesalv.chatrpg.domain.enums.Intent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Persona {

    private String id;
    private String name;
    private Intent intent;
    private String personality;
    private String ownerDiscordId;
    private String ownerUsername;
    private String visibility;
    private Boolean isMultiplayer;
    private List<String> writePermissions;
    private List<String> readPermissions;
    private Nudge nudge;
    private Bump bump;

    public static Persona defaultPersona() {

        return Persona.builder()
                .id("0")
                .name("DEFAULT PERSONA")
                .intent(Intent.CHAT)
                .visibility("private")
                .isMultiplayer(false)
                .build();
    }
}
