package es.thalesalv.chatrpg.testutils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import es.thalesalv.chatrpg.adapters.data.entity.LorebookEntryEntity;
import es.thalesalv.chatrpg.adapters.data.entity.WorldEntity;
import es.thalesalv.chatrpg.domain.enums.Visibility;
import es.thalesalv.chatrpg.domain.model.bot.LorebookEntry;
import es.thalesalv.chatrpg.domain.model.bot.World;

public class WorldTestUtils {

    private static final String NANO_ID = "241OZASGM6CESV7";

    public static World buildSimplePublicWorld() {

        final List<String> emptyPermissions = new ArrayList<>();
        final List<LorebookEntry> lorebook = new ArrayList<>();
        lorebook.add(buildSimpleLorebookEntry());
        lorebook.add(buildSimpleLorebookEntry());
        lorebook.add(buildSimpleLorebookEntry());
        lorebook.add(buildSimpleLorebookEntry());

        return World.builder()
                .id(NANO_ID)
                .name("Test world")
                .description("This is a test world")
                .initialPrompt("This is an initial prompt")
                .lorebook(lorebook)
                .ownerDiscordId("1083867535658725536")
                .visibility("public")
                .writePermissions(emptyPermissions)
                .readPermissions(emptyPermissions)
                .build();
    }

    public static WorldEntity buildSimplePublicWorldEntity() {

        final List<String> emptyPermissions = new ArrayList<>();
        final List<LorebookEntryEntity> lorebook = new ArrayList<>();
        lorebook.add(buildSimpleLorebookEntryEntity());
        lorebook.add(buildSimpleLorebookEntryEntity());
        lorebook.add(buildSimpleLorebookEntryEntity());
        lorebook.add(buildSimpleLorebookEntryEntity());

        return WorldEntity.builder()
                .id(NANO_ID)
                .name("Test world")
                .description("This is a test world")
                .initialPrompt("This is an initial prompt")
                .lorebook(lorebook)
                .ownerDiscordId("1083867535658725536")
                .visibility("public")
                .writePermissions(emptyPermissions)
                .readPermissions(emptyPermissions)
                .build();
    }

    public static List<World> buildSimplePublicWorldList() {

        final List<String> emptyPermissions = new ArrayList<>();
        final List<String> permissions = new ArrayList<>();
        permissions.add("302796314822049793");

        final World privateWorld = buildSimplePublicWorld();
        final World someonesElsesPublicWorld = buildSimplePublicWorld();
        final World someonesElsesPrivateWorld = buildSimplePublicWorld();
        final World someonesElsesPrivateWorldWithWritePersmissions = buildSimplePublicWorld();
        final World someonesElsesPrivateWorldWithReadPermissions = buildSimplePublicWorld();
        final World someonesElsesPrivateWorldWithAllPermissions = buildSimplePublicWorld();
        final World someonesElsesPublicWorldWithAllPermissions = buildSimplePublicWorld();
        final World ownPublicWorldWithAllPermissions = buildSimplePublicWorld();
        final World ownPrivateWorldWithAllPermissions = buildSimplePublicWorld();

        privateWorld.setVisibility("private");
        someonesElsesPrivateWorld.setWritePermissions(emptyPermissions);
        someonesElsesPrivateWorld.setReadPermissions(emptyPermissions);
        someonesElsesPublicWorld.setWritePermissions(emptyPermissions);
        someonesElsesPublicWorld.setReadPermissions(emptyPermissions);
        someonesElsesPrivateWorld.setWritePermissions(emptyPermissions);
        someonesElsesPrivateWorld.setReadPermissions(emptyPermissions);
        privateWorld.setWritePermissions(emptyPermissions);
        privateWorld.setReadPermissions(emptyPermissions);

        someonesElsesPrivateWorld.setVisibility("private");
        someonesElsesPrivateWorld.setOwnerDiscordId("463004243411075072");
        someonesElsesPublicWorld.setOwnerDiscordId("463004243411075072");

        ownPublicWorldWithAllPermissions.setWritePermissions(permissions);
        ownPublicWorldWithAllPermissions.setReadPermissions(permissions);
        ownPublicWorldWithAllPermissions.setOwnerDiscordId("302796314822049793");
        ownPublicWorldWithAllPermissions.setVisibility("private");

        ownPrivateWorldWithAllPermissions.setWritePermissions(permissions);
        ownPrivateWorldWithAllPermissions.setReadPermissions(permissions);
        ownPrivateWorldWithAllPermissions.setOwnerDiscordId("302796314822049793");
        ownPrivateWorldWithAllPermissions.setVisibility("private");

        someonesElsesPrivateWorldWithAllPermissions.setWritePermissions(permissions);
        someonesElsesPrivateWorldWithAllPermissions.setReadPermissions(permissions);
        someonesElsesPrivateWorldWithAllPermissions.setOwnerDiscordId("463004243411075072");
        someonesElsesPrivateWorldWithAllPermissions.setVisibility("private");

        someonesElsesPublicWorldWithAllPermissions.setWritePermissions(permissions);
        someonesElsesPublicWorldWithAllPermissions.setReadPermissions(permissions);
        someonesElsesPublicWorldWithAllPermissions.setOwnerDiscordId("463004243411075072");
        someonesElsesPublicWorldWithAllPermissions.setVisibility("public");

        someonesElsesPrivateWorldWithWritePersmissions.setVisibility("private");
        someonesElsesPrivateWorldWithReadPermissions.setVisibility("private");
        someonesElsesPrivateWorldWithWritePersmissions.setOwnerDiscordId("463004243411075072");
        someonesElsesPrivateWorldWithReadPermissions.setOwnerDiscordId("463004243411075072");
        someonesElsesPrivateWorldWithWritePersmissions.setWritePermissions(permissions);
        someonesElsesPrivateWorldWithReadPermissions.setReadPermissions(permissions);

        final List<World> worlds = new ArrayList<>();
        worlds.add(buildSimplePublicWorld());
        worlds.add(privateWorld);
        worlds.add(someonesElsesPublicWorld);
        worlds.add(someonesElsesPrivateWorld);
        worlds.add(someonesElsesPrivateWorldWithWritePersmissions);
        worlds.add(someonesElsesPrivateWorldWithReadPermissions);
        worlds.add(someonesElsesPrivateWorldWithAllPermissions);
        worlds.add(someonesElsesPublicWorldWithAllPermissions);
        worlds.add(ownPublicWorldWithAllPermissions);
        worlds.add(ownPrivateWorldWithAllPermissions);

        return worlds;
    }

    public static List<WorldEntity> buildSimplePublicWorldEntityList() {

        final List<String> emptyPermissions = new ArrayList<>();
        final List<String> permissions = new ArrayList<>();
        permissions.add("302796314822049793");

        final WorldEntity privateWorld = buildSimplePublicWorldEntity();
        final WorldEntity someonesElsesPublicWorld = buildSimplePublicWorldEntity();
        final WorldEntity someonesElsesPrivateWorld = buildSimplePublicWorldEntity();
        final WorldEntity someonesElsesPrivateWorldWithWritePersmissions = buildSimplePublicWorldEntity();
        final WorldEntity someonesElsesPrivateWorldWithReadPermissions = buildSimplePublicWorldEntity();
        final WorldEntity someonesElsesPrivateWorldWithAllPermissions = buildSimplePublicWorldEntity();
        final WorldEntity someonesElsesPublicWorldWithAllPermissions = buildSimplePublicWorldEntity();
        final WorldEntity ownPublicWorldWithAllPermissions = buildSimplePublicWorldEntity();
        final WorldEntity ownPrivateWorldWithAllPermissions = buildSimplePublicWorldEntity();

        privateWorld.setVisibility("private");
        someonesElsesPrivateWorldWithReadPermissions.setWritePermissions(emptyPermissions);
        someonesElsesPrivateWorldWithWritePersmissions.setReadPermissions(emptyPermissions);
        someonesElsesPrivateWorld.setWritePermissions(emptyPermissions);
        someonesElsesPrivateWorld.setReadPermissions(emptyPermissions);
        someonesElsesPublicWorld.setWritePermissions(emptyPermissions);
        someonesElsesPublicWorld.setReadPermissions(emptyPermissions);
        someonesElsesPrivateWorld.setWritePermissions(emptyPermissions);
        someonesElsesPrivateWorld.setReadPermissions(emptyPermissions);
        privateWorld.setWritePermissions(emptyPermissions);
        privateWorld.setReadPermissions(emptyPermissions);

        someonesElsesPrivateWorld.setVisibility("private");
        someonesElsesPrivateWorld.setOwnerDiscordId("463004243411075072");
        someonesElsesPublicWorld.setOwnerDiscordId("463004243411075072");

        ownPublicWorldWithAllPermissions.setWritePermissions(permissions);
        ownPublicWorldWithAllPermissions.setReadPermissions(permissions);
        ownPublicWorldWithAllPermissions.setOwnerDiscordId("302796314822049793");
        ownPublicWorldWithAllPermissions.setVisibility("private");

        ownPrivateWorldWithAllPermissions.setWritePermissions(permissions);
        ownPrivateWorldWithAllPermissions.setReadPermissions(permissions);
        ownPrivateWorldWithAllPermissions.setOwnerDiscordId("302796314822049793");
        ownPrivateWorldWithAllPermissions.setVisibility("private");

        someonesElsesPrivateWorldWithAllPermissions.setWritePermissions(permissions);
        someonesElsesPrivateWorldWithAllPermissions.setReadPermissions(permissions);
        someonesElsesPrivateWorldWithAllPermissions.setOwnerDiscordId("463004243411075072");
        someonesElsesPrivateWorldWithAllPermissions.setVisibility("private");

        someonesElsesPublicWorldWithAllPermissions.setWritePermissions(permissions);
        someonesElsesPublicWorldWithAllPermissions.setReadPermissions(permissions);
        someonesElsesPublicWorldWithAllPermissions.setOwnerDiscordId("463004243411075072");
        someonesElsesPublicWorldWithAllPermissions.setVisibility("public");

        someonesElsesPrivateWorldWithWritePersmissions.setVisibility("private");
        someonesElsesPrivateWorldWithReadPermissions.setVisibility("private");
        someonesElsesPrivateWorldWithWritePersmissions.setOwnerDiscordId("463004243411075072");
        someonesElsesPrivateWorldWithReadPermissions.setOwnerDiscordId("463004243411075072");
        someonesElsesPrivateWorldWithWritePersmissions.setWritePermissions(permissions);
        someonesElsesPrivateWorldWithReadPermissions.setReadPermissions(permissions);

        final List<WorldEntity> worlds = new ArrayList<>();
        worlds.add(buildSimplePublicWorldEntity());
        worlds.add(privateWorld);
        worlds.add(someonesElsesPublicWorld);
        worlds.add(someonesElsesPrivateWorld);
        worlds.add(someonesElsesPrivateWorldWithWritePersmissions);
        worlds.add(someonesElsesPrivateWorldWithReadPermissions);
        worlds.add(someonesElsesPrivateWorldWithAllPermissions);
        worlds.add(someonesElsesPublicWorldWithAllPermissions);
        worlds.add(ownPublicWorldWithAllPermissions);
        worlds.add(ownPrivateWorldWithAllPermissions);

        return worlds;
    }

    public static LorebookEntry buildSimpleLorebookEntry() {

        return LorebookEntry.builder()
                .id(NANO_ID)
                .name("Test entry")
                .description("Test description")
                .regex("test")
                .build();
    }

    public static LorebookEntryEntity buildSimpleLorebookEntryEntity() {

        return LorebookEntryEntity.builder()
                .id(NANO_ID)
                .name("Test entry")
                .regex("test")
                .description("Test description")
                .build();
    }

    public static boolean hasReadPermissions(final World world, final String userId) {

        final boolean isPublic = Visibility.isPublic(world.getVisibility());
        final boolean isOwner = world.getOwnerDiscordId()
                .equals(userId);

        final List<String> readPermissions = Optional.ofNullable(world.getReadPermissions())
                .orElse(Collections.emptyList());

        final List<String> writePermissions = Optional.ofNullable(world.getWritePermissions())
                .orElse(Collections.emptyList());

        final boolean canRead = readPermissions.contains(userId) || writePermissions.contains(userId);
        return isPublic || (isOwner || canRead);
    }

    public static boolean hasWritePermissions(final World world, final String userId) {

        final List<String> writePermissions = Optional.ofNullable(world.getWritePermissions())
                .orElse(Collections.emptyList());

        final boolean isOwner = world.getOwnerDiscordId()
                .equals(userId);

        final boolean canWrite = writePermissions.contains(userId);
        return isOwner || canWrite;
    }
}
