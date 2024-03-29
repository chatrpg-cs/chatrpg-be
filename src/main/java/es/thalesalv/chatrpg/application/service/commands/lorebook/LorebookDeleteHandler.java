package es.thalesalv.chatrpg.application.service.commands.lorebook;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import es.thalesalv.chatrpg.adapters.data.repository.ChannelRepository;
import es.thalesalv.chatrpg.application.mapper.chconfig.ChannelEntityToDTO;
import es.thalesalv.chatrpg.application.service.WorldService;
import es.thalesalv.chatrpg.application.util.ContextDatastore;
import es.thalesalv.chatrpg.domain.exception.LorebookEntryNotFoundException;
import es.thalesalv.chatrpg.domain.exception.MissingRequiredSlashCommandOptionException;
import es.thalesalv.chatrpg.domain.model.EventData;
import es.thalesalv.chatrpg.domain.model.bot.LorebookEntry;
import es.thalesalv.chatrpg.domain.model.bot.World;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

@Component
@Transactional
@RequiredArgsConstructor
public class LorebookDeleteHandler {

    private final ContextDatastore contextDatastore;
    private final ChannelEntityToDTO channelEntityToDTO;
    private final ChannelRepository channelRepository;
    private final WorldService worldService;

    private static final int DELETE_EPHEMERAL_TIMER = 20;
    private static final String MODAL_ID = "lb-delete";
    private static final String CHANNEL_CONFIG_NOT_FOUND = "The requested channel configuration could not be found";
    private static final String LORE_ENTRY_DELETED = "Lore entry deleted.";
    private static final String QUERIED_ENTRY_NOT_FOUND = "The entry queried does not exist.";
    private static final String USER_UPDATE_WITHOUT_ID = "User tried to use update command without ID";
    private static final String UNKNOWN_ERROR_CAUGHT = "Exception caught while deleting lorebook entry";
    private static final String DELETION_CANCELED = "Deletion action canceled. Entry has not been deleted.";
    private static final String ERROR_DELETE = "There was an error parsing your request. Please try again.";
    private static final String USER_DELETE_ENTRY_NOT_FOUND = "User tried to delete an entry that does not exist";
    private static final String MISSING_ID_MESSAGE = "The ID of the entry is required for a delete action. Please try again with the entry id.";
    private static final Logger LOGGER = LoggerFactory.getLogger(LorebookDeleteHandler.class);

    public void handleCommand(final SlashCommandInteractionEvent event) {

        try {
            LOGGER.debug("Received slash command for lore entry deletion");
            final String entryId = event.getOption("id")
                    .getAsString();

            final String eventAuthorId = event.getUser()
                    .getId();

            channelRepository.findById(event.getChannel()
                    .getId())
                    .map(channelEntityToDTO)
                    .ifPresentOrElse(channel -> {
                        final World world = channel.getChannelConfig()
                                .getWorld();

                        checkPermissions(world, event);
                        final LorebookEntry entry = worldService.retrieveLorebookEntryById(entryId, eventAuthorId);
                        contextDatastore.setEventData(EventData.builder()
                                .lorebookEntry(entry)
                                .build());

                        final Modal modal = buildEntryDeletionModal();
                        event.replyModal(modal)
                                .queue();

                    }, () -> event.reply(CHANNEL_CONFIG_NOT_FOUND)
                            .setEphemeral(true)
                            .queue(reply -> reply.deleteOriginal()
                                    .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS)));
        } catch (LorebookEntryNotFoundException e) {
            LOGGER.info(USER_DELETE_ENTRY_NOT_FOUND);
            event.reply(QUERIED_ENTRY_NOT_FOUND)
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
        } catch (MissingRequiredSlashCommandOptionException e) {
            LOGGER.info(USER_UPDATE_WITHOUT_ID);
            event.reply(MISSING_ID_MESSAGE)
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
        } catch (Exception e) {
            LOGGER.error(UNKNOWN_ERROR_CAUGHT, e);
            event.reply(ERROR_DELETE)
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
        }
    }

    public void handleModal(final ModalInteractionEvent event) {

        LOGGER.debug("Received data from lore entry deletion modal");
        event.deferReply();
        final String eventAuthorId = event.getUser()
                .getId();

        final boolean isUserSure = Optional.ofNullable(event.getValue(MODAL_ID))
                .filter(a -> a.getAsString()
                        .equals("y"))
                .isPresent();

        if (isUserSure) {
            final LorebookEntry entry = contextDatastore.getEventData()
                    .getLorebookEntry();

            worldService.deleteLorebookEntry(entry.getId(), eventAuthorId);
            event.reply(LORE_ENTRY_DELETED)
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
            return;
        }
        event.reply(DELETION_CANCELED)
                .setEphemeral(true)
                .queue(m -> m.deleteOriginal()
                        .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
    }

    private Modal buildEntryDeletionModal() {

        LOGGER.debug("Building entry deletion modal");
        final TextInput deleteLoreEntry = TextInput
                .create(MODAL_ID, "Are you sure you want to delete this entry?", TextInputStyle.SHORT)
                .setPlaceholder("y or n")
                .setMaxLength(1)
                .setRequired(true)
                .build();

        return Modal.create(MODAL_ID, "Delete lore entry")
                .addComponents(ActionRow.of(deleteLoreEntry))
                .build();
    }

    private void checkPermissions(World world, SlashCommandInteractionEvent event) {

        final String userId = event.getUser()
                .getId();

        final boolean isPrivate = world.getVisibility()
                .equals("private");

        final boolean isOwner = world.getOwnerDiscordId()
                .equals(userId);

        final boolean canWrite = world.getWritePermissions()
                .contains(userId);

        final boolean isAllowed = isOwner || canWrite;
        if (isPrivate && !isAllowed) {
            event.reply("You don't have permission from the owner of this private world to modify it")
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
        }
    }
}
