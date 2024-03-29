package es.thalesalv.chatrpg.application.service.commands.lorebook;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;

import es.thalesalv.chatrpg.application.service.WorldService;
import es.thalesalv.chatrpg.domain.model.bot.World;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;

@Component
@Transactional
@RequiredArgsConstructor
public class LorebookListHandler {

    private final ObjectWriter prettyPrintObjectMapper;
    private final WorldService worldService;

    private static final Logger LOGGER = LoggerFactory.getLogger(LorebookListHandler.class);

    private static final int DELETE_EPHEMERAL_TIMER = 20;
    private static final String ERROR_SERIALIZATION = "Error serializing entry data.";
    private static final String ERROR_HANDLING_ENTRY = "Error handling lore entries file.";
    private static final String ERROR_RETRIEVE = "An error occurred while retrieving lorebook data";
    private static final String USER_ERROR_RETRIEVE = "There was an error parsing your request. Please try again.";

    public void handleCommand(SlashCommandInteractionEvent event) {

        try {
            LOGGER.debug("Received slash command for lore entry retrieval");
            event.deferReply();
            try {
                retrieveAllLorebooks(event);
            } catch (JsonProcessingException e) {
                LOGGER.error(ERROR_SERIALIZATION, e);
                event.reply(ERROR_RETRIEVE)
                        .setEphemeral(true)
                        .queue(m -> m.deleteOriginal()
                                .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
            } catch (IOException e) {
                LOGGER.error(ERROR_HANDLING_ENTRY, e);
                event.reply(ERROR_RETRIEVE)
                        .setEphemeral(true)
                        .queue(m -> m.deleteOriginal()
                                .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
            }
        } catch (Exception e) {
            LOGGER.error(ERROR_RETRIEVE, e);
            event.reply(USER_ERROR_RETRIEVE)
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
        }
    }

    private void retrieveAllLorebooks(final SlashCommandInteractionEvent event) throws IOException {

        final String eventAuthorId = event.getUser()
                .getId();

        final List<World> lorebooks = worldService.retrieveAllWorlds(eventAuthorId)
                .stream()
                .map(w -> {
                    w.setInitialPrompt(null);
                    final String ownerName = event.getJDA()
                            .retrieveUserById(w.getOwnerDiscordId())
                            .complete()
                            .getName();

                    w.setOwnerDiscordId(ownerName);
                    return w;
                })
                .toList();

        if (lorebooks.isEmpty()) {
            event.reply("There are no lorebooks to show.")
                    .setEphemeral(true)
                    .complete();

            return;
        }

        final String lorebookJson = prettyPrintObjectMapper.writeValueAsString(lorebooks);
        final File file = File.createTempFile("lorebooks-", ".json");
        Files.write(file.toPath(), lorebookJson.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
        final FileUpload fileUpload = FileUpload.fromData(file);

        event.replyFiles(fileUpload)
                .setEphemeral(true)
                .complete();

        fileUpload.close();
    }
}
