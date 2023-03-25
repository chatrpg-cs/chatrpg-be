package es.thalesalv.chatrpg.application.service.commands.chconfig;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.adapters.data.db.repository.ChannelRepository;
import es.thalesalv.chatrpg.application.service.commands.DiscordCommand;
import es.thalesalv.chatrpg.domain.exception.ChannelConfigurationNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

@Service
@Transactional
@RequiredArgsConstructor
public class UnsetChConfigCommandService implements DiscordCommand {

    private final ChannelRepository channelRepository;

    private static final int DELETE_EPHEMERAL_20_SECONDS = 20;

    private static final String USER_COMMAND_CHCONFIG_NOT_FOUND = "User tried to delete a config from a channel that has no config attached to it";
    private static final String CONFIG_ID_NOT_FOUND = "There is no channel configuration attached to this channel.";
    private static final String WORLD_UNLINKED_CHANNEL_CONFIG = "World `{0}` was unlinked from configuration ID `{1}`";
    private static final String CHANNEL_UNLINKED_CONFIG = "This channel has been unlinked from configurations.";
    private static final String ERROR_SETTING_CHANNEL_CONFIG = "Error unsetting channel config";
    private static final String SOMETHING_WRONG_TRY_AGAIN = "Something went wrong when unsetting the channel config. Please try again.";

    private static final Logger LOGGER = LoggerFactory.getLogger(UnsetChConfigCommandService.class);

    @Override
    public void handle(final SlashCommandInteractionEvent event) {

        LOGGER.debug("Received slash command for unsetting channel config or world");
        try {
            event.deferReply();
            Optional.ofNullable(event.getOption("type"))
                    .map(OptionMapping::getAsString)
                    .map(type -> {
                        switch (type) {
                            case "channel":
                                deleteChannelConfig(event);
                                break;
                            case "world":
                                unsetWorld(event);
                                break;
                            default:
                                throw new RuntimeException("Option provided not found");
                        }

                        return type;
                    })
                    .orElseThrow(() -> new RuntimeException("No option provided."));
        } catch (ChannelConfigurationNotFoundException e) {
            LOGGER.debug(USER_COMMAND_CHCONFIG_NOT_FOUND, e);
            event.reply(CONFIG_ID_NOT_FOUND).setEphemeral(true).queue(reply -> {
                reply.deleteOriginal().queueAfter(DELETE_EPHEMERAL_20_SECONDS, TimeUnit.SECONDS);
            });
        } catch (Exception e) {
            LOGGER.error(ERROR_SETTING_CHANNEL_CONFIG, e);
            event.reply(SOMETHING_WRONG_TRY_AGAIN).setEphemeral(true).queue(reply -> {
                reply.deleteOriginal().queueAfter(DELETE_EPHEMERAL_20_SECONDS, TimeUnit.SECONDS);
            });
        }
    }

    private void deleteChannelConfig(final SlashCommandInteractionEvent event) {

        LOGGER.debug("Deleting channel config, currently attached to channel {} (channel ID {})",
                event.getChannel().getName(), event.getChannel().getId());

        channelRepository.deleteByChannelId(event.getChannel().getId());
        event.reply(MessageFormat.format(CHANNEL_UNLINKED_CONFIG, event.getChannel().getName()))
                .setEphemeral(true).queue(reply -> {
                    reply.deleteOriginal().queueAfter(DELETE_EPHEMERAL_20_SECONDS, TimeUnit.SECONDS);
                });
    }

    private void unsetWorld(final SlashCommandInteractionEvent event) {

        LOGGER.debug("Detaching world from channel config {} (channel ID {})",
                event.getChannel().getName(), event.getChannel().getId());

        channelRepository.findByChannelId(event.getChannel().getId())
                .map(config -> {
                    final String worldName = config.getChannelConfig().getWorld().getName();
                    config.getChannelConfig().setWorld(null);
                    event.reply(
                            MessageFormat.format(WORLD_UNLINKED_CHANNEL_CONFIG, worldName, config.getId()))
                            .setEphemeral(true).queue(reply -> {
                                reply.deleteOriginal().queueAfter(DELETE_EPHEMERAL_20_SECONDS, TimeUnit.SECONDS);
                            });

                    return config;
                })
                .orElseThrow(() -> new ChannelConfigurationNotFoundException(CONFIG_ID_NOT_FOUND));
    }
}
