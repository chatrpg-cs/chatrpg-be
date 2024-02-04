package es.thalesalv.chatrpg.core.domain.world;

import java.util.ArrayList;
import java.util.List;

import es.thalesalv.chatrpg.core.domain.Permissions;
import es.thalesalv.chatrpg.core.domain.PermissionsFixture;
import es.thalesalv.chatrpg.core.domain.Visibility;

public class WorldFixture {

    public static World.Builder publicWorld() {

        World.Builder builder = World.builder();
        builder.id("857345HAA");
        builder.name("ChatRPG");
        builder.description("This is an RPG world");
        builder.adventureStart("As you enter the city, people around you start looking at you.");
        builder.visibility(Visibility.fromString("PUBLIC"));

        Permissions permissions = PermissionsFixture.samplePermissions().build();
        builder.permissions(permissions);

        List<LorebookEntry> lorebook = new ArrayList<>();
        lorebook.add(LorebookEntryFixture.sampleLorebookEntry().build());
        lorebook.add(LorebookEntryFixture.samplePlayerCharacterLorebookEntry().build());
        builder.lorebook(lorebook);

        return builder;
    }

    public static World.Builder privateWorld() {

        World.Builder builder = World.builder();
        builder.id("857345HAA");
        builder.name("ChatRPG");
        builder.description("This is an RPG world");
        builder.adventureStart("As you enter the city, people around you start looking at you.");
        builder.visibility(Visibility.fromString("PRIVATE"));

        Permissions permissions = PermissionsFixture.samplePermissions().build();
        builder.permissions(permissions);

        List<LorebookEntry> lorebook = new ArrayList<>();
        lorebook.add(LorebookEntryFixture.sampleLorebookEntry().build());
        lorebook.add(LorebookEntryFixture.samplePlayerCharacterLorebookEntry().build());
        builder.lorebook(lorebook);

        return builder;
    }
}
