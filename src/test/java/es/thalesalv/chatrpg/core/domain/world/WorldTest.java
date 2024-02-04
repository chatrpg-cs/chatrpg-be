package es.thalesalv.chatrpg.core.domain.world;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import es.thalesalv.chatrpg.common.exception.BusinessRuleViolationException;
import es.thalesalv.chatrpg.core.domain.Permissions;
import es.thalesalv.chatrpg.core.domain.PermissionsFixture;

public class WorldTest {

    @Test
    public void makeWorldPublic() {

        // Given
        World world = WorldFixture.privateWorld().build();

        // When
        world.makePublic();

        // Then
        assertThat(world.isPublic()).isTrue();
    }

    @Test
    public void makeWorldPrivate() {

        // Given
        World world = WorldFixture.publicWorld().build();

        // When
        world.makePrivate();

        // Then
        assertThat(world.isPublic()).isFalse();
    }

    @Test
    public void updateWorldName() {

        // Given
        World world = WorldFixture.publicWorld().build();

        // When
        world.updateName("New Name");

        // Then
        assertThat(world.getName()).isEqualTo("New Name");
    }

    @Test
    public void updateWorldDescription() {

        // Given
        World world = WorldFixture.publicWorld().build();

        // When
        world.updateDescription("New Description");

        // Then
        assertThat(world.getDescription()).isEqualTo("New Description");
    }

    @Test
    public void updateWorldInitialPrompt() {

        // Given
        World world = WorldFixture.publicWorld().build();

        // When
        world.updateAdventureStart("New Prompt");

        // Then
        assertThat(world.getAdventureStart()).isEqualTo("New Prompt");
    }

    @Test
    public void errorWhenCreatingWorldWithNullName() {

        // Given
        World.Builder worldBuilder = WorldFixture.publicWorld().name(null);

        // Then
        assertThrows(BusinessRuleViolationException.class, worldBuilder::build);
    }

    @Test
    public void errorWhenCreatingWorldWithEmptyName() {

        // Given
        World.Builder worldBuilder = WorldFixture.publicWorld().name(EMPTY);

        // Then
        assertThrows(BusinessRuleViolationException.class, worldBuilder::build);
    }

    @Test
    public void errorWhenCreatingWorldWithNullPermissions() {

        // Given
        World.Builder worldBuilder = WorldFixture.publicWorld().permissions(null);

        // Then
        assertThrows(BusinessRuleViolationException.class, worldBuilder::build);
    }

    @Test
    public void errorWhenCreatingWorldWithNullVisibility() {

        // Given
        World.Builder worldBuilder = WorldFixture.publicWorld().visibility(null);

        // Then
        assertThrows(BusinessRuleViolationException.class, worldBuilder::build);
    }

    @Test
    public void errorWhenModifyingLorebookDirectly() {

        // Given
        World world = WorldFixture.publicWorld().build();
        LorebookEntry entry = LorebookEntryFixture.sampleLorebookEntry().build();

        // Then
        assertThrows(UnsupportedOperationException.class,
                () -> world.getLorebook().add(entry));
    }

    @Test
    public void addLorebookEntry() {

        // Given
        World world = WorldFixture.publicWorld().build();
        LorebookEntry entry = LorebookEntryFixture.sampleLorebookEntry().build();

        // When
        world.addToLorebook(entry);

        // Then
        assertThat(world.getLorebook())
                .isNotNull()
                .isNotEmpty()
                .contains(entry);
    }

    @Test
    public void removeLorebookEntry() {

        // Given
        World world = WorldFixture.publicWorld().build();
        LorebookEntry entry = LorebookEntryFixture.sampleLorebookEntry().build();

        // When
        world.removeFromLorebook(entry);

        // Then
        assertThat(world.getLorebook())
                .isNotNull()
                .isNotEmpty()
                .doesNotContain(entry);
    }

    @Test
    public void addWriterToList() {

        // Given
        String userId = "1234567890";
        World.Builder worldBuilder = WorldFixture.publicWorld();
        Permissions permissions = PermissionsFixture.samplePermissions()
                .usersAllowedToWrite(new ArrayList<>()).build();

        worldBuilder.permissions(permissions);

        World world = worldBuilder.build();

        // When
        world.addWriterUser(userId);

        // Then
        assertThat(world.getWriterUsers()).contains(userId);
    }

    @Test
    public void addReaderToList() {

        // Given
        String userId = "1234567890";
        World.Builder worldBuilder = WorldFixture.publicWorld();
        Permissions permissions = PermissionsFixture.samplePermissions()
                .usersAllowedToRead(new ArrayList<>()).build();

        worldBuilder.permissions(permissions);

        World world = worldBuilder.build();

        // When
        world.addReaderUser(userId);

        // Then
        assertThat(world.getReaderUsers()).contains(userId);
    }

    @Test
    public void removeReaderFromList() {

        // Given
        String userId = "1234567890";
        World.Builder worldBuilder = WorldFixture.publicWorld();

        List<String> usersAllowedToRead = new ArrayList<>();
        usersAllowedToRead.add(userId);

        Permissions permissions = PermissionsFixture.samplePermissions()
                .usersAllowedToRead(usersAllowedToRead).build();

        worldBuilder.permissions(permissions);

        World world = worldBuilder.build();

        // When
        world.removeReaderUser(userId);

        // Then
        assertThat(world.getReaderUsers()).doesNotContain(userId);
    }

    @Test
    public void removeWriterFromList() {

        // Given
        String userId = "1234567890";
        World.Builder worldBuilder = WorldFixture.publicWorld();

        List<String> usersAllowedToWrite = new ArrayList<>();
        usersAllowedToWrite.add(userId);

        Permissions permissions = PermissionsFixture.samplePermissions()
                .usersAllowedToWrite(usersAllowedToWrite).build();

        worldBuilder.permissions(permissions);

        World world = worldBuilder.build();

        // When
        world.removeWriterUser(userId);

        // Then
        assertThat(world.getWriterUsers()).doesNotContain(userId);
    }
}
