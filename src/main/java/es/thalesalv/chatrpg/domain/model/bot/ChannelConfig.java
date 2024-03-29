package es.thalesalv.chatrpg.domain.model.bot;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChannelConfig {

    private String id;
    private String name;
    private String ownerDiscordId;
    private String ownerUsername;
    private List<String> writePermissions;
    private List<String> readPermissions;
    private World world;
    private Persona persona;
    private ModelSettings modelSettings;
    private ModerationSettings moderationSettings;
}
