package es.thalesalv.chatrpg.application.service.commands.world;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;

import es.thalesalv.chatrpg.adapters.data.entity.WorldEntity;
import es.thalesalv.chatrpg.adapters.data.repository.ChannelRepository;
import es.thalesalv.chatrpg.adapters.data.repository.WorldRepository;
import es.thalesalv.chatrpg.application.mapper.chconfig.ChannelEntityToDTO;
import es.thalesalv.chatrpg.application.mapper.world.WorldEntityToDTO;
import es.thalesalv.chatrpg.application.service.commands.DiscordCommand;
import es.thalesalv.chatrpg.domain.exception.ChannelConfigNotFoundException;
import es.thalesalv.chatrpg.domain.exception.WorldNotFoundException;
import es.thalesalv.chatrpg.domain.model.chconf.World;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;

@Service
@Transactional
@RequiredArgsConstructor
public class ListWorldCommandService implements DiscordCommand {

    private final ObjectWriter prettyPrintObjectMapper;
    private final ChannelEntityToDTO channelEntityToDTO;
    private final WorldEntityToDTO worldEntityToDTO;

    private final WorldRepository worldRepository;
    private final ChannelRepository channelRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(GetWorldCommandService.class);

    private static final int DELETE_EPHEMERAL_20_SECONDS = 20;
    private static final String PUBLIC = "public";
    private static final String ERROR_SERIALIZATION = "Error serializing entry data.";
    private static final String CHANNEL_NO_CONFIG_ATTACHED = "This channel does not have a configuration with a valid world/lorebook attached to it.";
    private static final String CHANNEL_CONFIG_NOT_FOUND = "Channel does not have configuration attached";
    private static final String ERROR_RETRIEVE = "An error occurred while retrieving world data";
    private static final String USER_ERROR_RETRIEVE = "There was an error parsing your request. Please try again.";
    private static final String QUERIED_WORLD_NOT_FOUND = "The world queried does not exist.";
    private static final String ERROR_HANDLING_ENTRY = "Error handling lore entries file.";

    @Override
    public void handle(SlashCommandInteractionEvent event) {

        try {
            LOGGER.debug("Received slash command for lore entry retrieval");
            event.deferReply();
            channelRepository.findByChannelId(event.getChannel()
                    .getId())
                    .map(channelEntityToDTO)
                    .map(channel -> {
                        try {
                            List<World> worlds = worldRepository.findAll()
                                    .stream()
                                    .filter(filterWorlds(event))
                                    .map(worldEntityToDTO)
                                    .map(w -> cleanWorld(w, event))
                                    .collect(Collectors.toList());

                            final String worldJson = prettyPrintObjectMapper.writeValueAsString(worlds);
                            final File file = File.createTempFile("lore-entries-", ".json");
                            Files.write(file.toPath(), worldJson.getBytes(StandardCharsets.UTF_8),
                                    StandardOpenOption.APPEND);
                            final FileUpload fileUpload = FileUpload.fromData(file);
                            event.replyFiles(fileUpload)
                                    .setEphemeral(true)
                                    .complete();
                            fileUpload.close();
                        } catch (JsonProcessingException e) {
                            LOGGER.error(ERROR_SERIALIZATION, e);
                            event.reply(ERROR_RETRIEVE)
                                    .setEphemeral(true)
                                    .queue(m -> m.deleteOriginal()
                                            .queueAfter(DELETE_EPHEMERAL_20_SECONDS, TimeUnit.SECONDS));
                        } catch (IOException e) {
                            LOGGER.error(ERROR_HANDLING_ENTRY, e);
                            event.reply(ERROR_RETRIEVE)
                                    .setEphemeral(true)
                                    .queue(m -> m.deleteOriginal()
                                            .queueAfter(DELETE_EPHEMERAL_20_SECONDS, TimeUnit.SECONDS));
                        }

                        return channel;
                    })
                    .orElseThrow(ChannelConfigNotFoundException::new);
        } catch (WorldNotFoundException e) {
            LOGGER.info(QUERIED_WORLD_NOT_FOUND);
            event.reply(QUERIED_WORLD_NOT_FOUND)
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_20_SECONDS, TimeUnit.SECONDS));
        } catch (ChannelConfigNotFoundException e) {
            LOGGER.info(CHANNEL_CONFIG_NOT_FOUND);
            event.reply(CHANNEL_NO_CONFIG_ATTACHED)
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_20_SECONDS, TimeUnit.SECONDS));
        } catch (Exception e) {
            LOGGER.error(ERROR_RETRIEVE, e);
            event.reply(USER_ERROR_RETRIEVE)
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_20_SECONDS, TimeUnit.SECONDS));
        }
    }

    private Predicate<WorldEntity> filterWorlds(final SlashCommandInteractionEvent event) {

        return w -> w.getVisibility()
                .equals(PUBLIC)
                || w.getOwner()
                        .equals(event.getUser()
                                .getId());
    }

    private World cleanWorld(final World world, final SlashCommandInteractionEvent event) {

        final String ownerName = event.getJDA()
                .getUserById(world.getOwner())
                .getName();
        world.setOwner(ownerName);
        world.setLorebook(null);
        world.setInitialPrompt(null);
        return world;
    }
}
