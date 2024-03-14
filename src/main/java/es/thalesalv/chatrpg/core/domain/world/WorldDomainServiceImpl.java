package es.thalesalv.chatrpg.core.domain.world;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.common.exception.AssetNotFoundException;
import es.thalesalv.chatrpg.common.exception.BusinessRuleViolationException;
import es.thalesalv.chatrpg.core.application.command.world.CreateWorld;
import es.thalesalv.chatrpg.core.application.command.world.UpdateWorld;
import es.thalesalv.chatrpg.core.application.command.world.WorldLorebookEntry;
import es.thalesalv.chatrpg.core.domain.Permissions;
import es.thalesalv.chatrpg.core.domain.Visibility;
import es.thalesalv.chatrpg.core.domain.port.TokenizerPort;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class WorldDomainServiceImpl implements WorldDomainService {

    @Value("${chatrpg.validation.token-limits.world.initial-prompt}")
    private int adventureStartTokenLimit;

    private final WorldRepository repository;
    private final TokenizerPort tokenizerPort;

    @Override
    public World createFrom(CreateWorld command) {

        List<LorebookEntry> lorebookEntries = mapLorebookEntriesFromCommand(command.getLorebookEntries());
        Permissions permissions = Permissions.builder()
                .ownerDiscordId(command.getCreatorDiscordId())
                .usersAllowedToRead(command.getReaderUsers())
                .usersAllowedToWrite(command.getWriterUsers())
                .build();

        World world = World.builder()
                .name(command.getName())
                .description(command.getDescription())
                .adventureStart(command.getAdventureStart())
                .visibility(Visibility.fromString(command.getVisibility()))
                .permissions(permissions)
                .lorebook(lorebookEntries)
                .build();

        validateTokenCount(world);

        return repository.save(world);
    }

    @Override
    public World update(UpdateWorld command) {

        // TODO extract real ID from principal when API is ready
        repository.findById(command.getId(), "owner")
                .orElseThrow(() -> new AssetNotFoundException("World to be updated was not found"));

        List<LorebookEntry> lorebookEntries = mapLorebookEntriesFromCommand(command.getLorebookEntries());

        Permissions permissions = Permissions.builder()
                .ownerDiscordId(command.getCreatorDiscordId())
                .usersAllowedToRead(command.getReaderUsers())
                .usersAllowedToWrite(command.getWriterUsers())
                .build();

        World world = World.builder()
                .id(command.getId())
                .name(command.getName())
                .description(command.getDescription())
                .adventureStart(command.getAdventureStart())
                .visibility(Visibility.fromString(command.getVisibility()))
                .permissions(permissions)
                .lorebook(lorebookEntries)
                .build();

        validateTokenCount(world);

        return repository.save(world);
    }

    private void validateTokenCount(World world) {

        int adventureStartTokenCount = tokenizerPort.getTokenCountFrom(world.getAdventureStart());
        if (adventureStartTokenCount > adventureStartTokenLimit) {
            throw new BusinessRuleViolationException("Amount of tokens in initial prompt surpasses allowed limit");
        }
    }

    private List<LorebookEntry> mapLorebookEntriesFromCommand(List<WorldLorebookEntry> commandLorebookEntries) {

        if (commandLorebookEntries == null) {
            return Collections.emptyList();
        }

        return commandLorebookEntries.stream()
                .map(this::mapLorebookEntryFromCommand)
                .toList();
    }

    private LorebookEntry mapLorebookEntryFromCommand(WorldLorebookEntry commandLorebookEntry) {

        return LorebookEntry.builder()
                .name(commandLorebookEntry.getName())
                .regex(commandLorebookEntry.getRegex())
                .description(commandLorebookEntry.getDescription())
                .playerDiscordId(commandLorebookEntry.getPlayerDiscordId())
                .build();
    }
}
