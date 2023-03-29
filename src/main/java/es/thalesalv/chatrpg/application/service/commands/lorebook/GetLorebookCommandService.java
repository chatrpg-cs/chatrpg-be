package es.thalesalv.chatrpg.application.service.commands.lorebook;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;

import es.thalesalv.chatrpg.adapters.data.repository.ChannelRepository;
import es.thalesalv.chatrpg.application.mapper.chconfig.ChannelEntityToDTO;
import es.thalesalv.chatrpg.application.service.commands.DiscordCommand;
import es.thalesalv.chatrpg.domain.exception.ChannelConfigNotFoundException;
import es.thalesalv.chatrpg.domain.exception.LorebookEntryNotFoundException;
import es.thalesalv.chatrpg.domain.model.chconf.Lorebook;
import es.thalesalv.chatrpg.domain.model.chconf.LorebookEntry;
import es.thalesalv.chatrpg.domain.model.chconf.World;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

@Service
@Transactional
@RequiredArgsConstructor
public class GetLorebookCommandService implements DiscordCommand {

    private final ObjectWriter prettyPrintObjectMapper;
    private final ChannelEntityToDTO channelEntityToDTO;
    private final ChannelRepository channelRepository;
    private static final int DELETE_EPHEMERAL_20_SECONDS = 20;
    private static final String CHANNEL_NO_CONFIG_ATTACHED = "This channel does not have a configuration with a valid world/lorebook attached to it.";
    private static final String CHANNEL_CONFIG_NOT_FOUND = "Channel does not have configuration attached";
    private static final String ERROR_SERIALIZATION = "Error serializing entry data.";
    private static final String ENTRY_RETRIEVED = "Retrieved lore entry with name **{0}**.\n```json\n{1}```";
    private static final String LOREBOOK_RETRIEVED = "Retrieved lorebook with name **{0}**.\n```json\n{1}```";
    private static final String ERROR_RETRIEVE = "An error occurred while retrieving lorebook data";
    private static final String USER_ERROR_RETRIEVE = "There was an error parsing your request. Please try again.";
    private static final String QUERIED_ENTRY_NOT_FOUND = "The entry queried does not exist.";
    private static final Logger LOGGER = LoggerFactory.getLogger(GetLorebookCommandService.class);

    @Override
    public void handle(final SlashCommandInteractionEvent event) {

        try {
            LOGGER.debug("Received slash command for lore entry retrieval");
            event.deferReply();
            channelRepository.findByChannelId(event.getChannel()
                    .getId())
                    .map(channelEntityToDTO)
                    .map(channel -> {
                        try {
                            final World world = channel.getChannelConfig()
                                    .getWorld();
                            final OptionMapping entryId = event.getOption("id");
                            if (entryId != null) {
                                retrieveLoreEntryById(entryId.getAsString(), world, event);
                                return channel;
                            }

                            retrieveLorebook(world, event);
                            ;
                            return channel;
                        } catch (JsonProcessingException e) {
                            LOGGER.error(ERROR_SERIALIZATION, e);
                            event.reply(ERROR_RETRIEVE)
                                    .setEphemeral(true)
                                    .queue(m -> m.deleteOriginal()
                                            .queueAfter(DELETE_EPHEMERAL_20_SECONDS, TimeUnit.SECONDS));
                        }
                        return channel;
                    })
                    .orElseThrow(ChannelConfigNotFoundException::new);
        } catch (LorebookEntryNotFoundException e) {
            LOGGER.info(QUERIED_ENTRY_NOT_FOUND);
            event.reply(QUERIED_ENTRY_NOT_FOUND)
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

    private void retrieveLorebook(final World world, final SlashCommandInteractionEvent event)
            throws JsonProcessingException {

        final Lorebook lorebook = world.getLorebook();
        lorebook.setEntries(null);
        lorebook.setOwner(event.getJDA()
                .getUserById(world.getOwner())
                .getName());
        final String lorebookJson = prettyPrintObjectMapper.writeValueAsString(lorebook);
        event.reply(MessageFormat.format(LOREBOOK_RETRIEVED, lorebook.getName(), lorebookJson))
                .setEphemeral(true)
                .complete();
    }

    private void retrieveLoreEntryById(final String entryId, final World world,
            final SlashCommandInteractionEvent event) throws JsonProcessingException {

        final LorebookEntry entry = world.getLorebook()
                .getEntries()
                .stream()
                .filter(e -> e.getId()
                        .equals(entryId))
                .findFirst()
                .orElseThrow(LorebookEntryNotFoundException::new);
        final String loreEntryJson = prettyPrintObjectMapper.writeValueAsString(entry);
        event.reply(MessageFormat.format(ENTRY_RETRIEVED, entry.getName(), loreEntryJson))
                .setEphemeral(true)
                .complete();
    }
}
