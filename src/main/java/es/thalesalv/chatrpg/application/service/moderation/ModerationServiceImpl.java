package es.thalesalv.chatrpg.application.service.moderation;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.adapters.rest.client.ModerationApiService;
import es.thalesalv.chatrpg.application.helper.MessageHelper;
import es.thalesalv.chatrpg.domain.exception.ModerationException;
import es.thalesalv.chatrpg.domain.model.EventData;
import es.thalesalv.chatrpg.domain.model.bot.ChannelConfig;
import es.thalesalv.chatrpg.domain.model.bot.ModerationSettings;
import es.thalesalv.chatrpg.domain.model.openai.moderation.ModerationRequest;
import es.thalesalv.chatrpg.domain.model.openai.moderation.ModerationResponse;
import es.thalesalv.chatrpg.domain.model.openai.moderation.ModerationResult;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Profile("!no-moderation")
public class ModerationServiceImpl implements ModerationService {

    @Value("${chatrpg.generation.default-threshold}")
    private double defaultThreshold;

    private final MessageHelper<String> messageHelper;
    private final ModerationApiService moderationApiService;

    private static final String UNSAFE_CONTENT_FOUND = "Unsafe content detected";
    private static final String FLAGGED_OUTPUT = "The AI generated outputs that were flagged as unsafe by OpenAI's moderation. Please edit the prompt so it doesn't contain unsafe content and try again. This message will disappear in a few seconds.";
    private static final String FLAGGED_MESSAGE = "The message you sent has content that was flagged by OpenAI's moderation. Your message has been deleted from the conversation channel.";
    private static final String FLAGGED_ENTRY = "Your lorebook entry has content that was flagged by OpenAI's moderation. It cannot be saved until it's edited to conform to OpenAI's safety standards.";
    private static final String FLAGGED_TOPICS_MESSAGE = "\n**Message content:** {0}\n**Flagged topics:** {1}";
    private static final String FLAGGED_TOPICS_LOREBOOK = "\n**Flagged topics:** {0}\n```json\n{1}```";
    private static final String FLAGGED_TOPICS_OUTPUT = "\n**Flagged topics:** {0}";

    private static final Logger LOGGER = LoggerFactory.getLogger(ModerationServiceImpl.class);

    @Override
    public Mono<ModerationResponse> moderateInteraction(final String content, final EventData eventData,
            final ModalInteractionEvent event) {

        if (StringUtils.isBlank(content))
            return Mono.just(ModerationResponse.builder()
                    .build());

        final ModerationRequest request = ModerationRequest.builder()
                .input(content)
                .build();

        return moderationApiService.callModeration(request)
                .doOnNext(response -> {
                    final ModerationResult moderationResult = response.getModerationResult()
                            .get(0);

                    checkModerationThresholds(moderationResult, eventData.getChannelDefinitions()
                            .getChannelConfig(), content);
                })
                .doOnError(ModerationException.class::isInstance, ex -> {
                    final ModerationException e = (ModerationException) ex;
                    handleFlags(e.getFlaggedTopics(), event, content);
                });
    }

    @Override
    public Mono<ModerationResponse> moderateInput(final List<String> messages, final EventData eventData) {

        final String prompt = messageHelper.stringifyMessages(messages);
        if (StringUtils.isBlank(prompt))
            return Mono.just(ModerationResponse.builder()
                    .build());

        final ModerationRequest request = ModerationRequest.builder()
                .input(prompt)
                .build();

        return moderationApiService.callModeration(request)
                .doOnNext(response -> {
                    final ModerationResult moderationResult = response.getModerationResult()
                            .get(0);

                    eventData.setInputModerationResult(moderationResult);
                    checkModerationThresholds(moderationResult, eventData.getChannelDefinitions()
                            .getChannelConfig(), prompt);
                })
                .doOnError(ModerationException.class::isInstance, ex -> {
                    final ModerationException e = (ModerationException) ex;
                    handleFlags(e.getFlaggedTopics(), eventData);
                });
    }

    @Override
    public Mono<ModerationResponse> moderateOutput(final String output, final EventData eventData) {

        if (StringUtils.isBlank(output))
            return Mono.just(ModerationResponse.builder()
                    .build());

        final ModerationRequest request = ModerationRequest.builder()
                .input(output)
                .build();

        return moderationApiService.callModeration(request)
                .doOnNext(response -> {
                    final ModerationResult moderationResult = response.getModerationResult()
                            .get(0);

                    eventData.setOutputModerationResult(moderationResult);
                    checkModerationThresholds(moderationResult, eventData.getChannelDefinitions()
                            .getChannelConfig(), output);
                })
                .doOnError(ModerationException.class::isInstance, ex -> {
                    final ModerationException e = (ModerationException) ex;
                    handleFlagsOutput(e.getFlaggedTopics(), eventData, e.getFlaggedContent());
                    throw new ModerationException(e);
                });
    }

    private void checkModerationThresholds(final ModerationResult moderationResult, final ChannelConfig channelConfig,
            final String prompt) {

        List<String> flaggedTopics = new ArrayList<>();
        final ModerationSettings moderationSettings = channelConfig.getModerationSettings();
        if (moderationSettings.isAbsolute() && moderationResult.getFlagged()
                .booleanValue()) {
            flaggedTopics = moderationResult.getCategories()
                    .entrySet()
                    .stream()
                    .filter(entry -> entry.getValue())
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        }

        if (!moderationSettings.isAbsolute()) {
            flaggedTopics = moderationResult.getCategoryScores()
                    .entrySet()
                    .stream()
                    .filter(entry -> {
                        final String correctedNumber = entry.getValue()
                                .replace(",", ".");

                        return Double.valueOf(correctedNumber) > Optional.ofNullable(moderationSettings.getThresholds()
                                .get(entry.getKey()))
                                .orElse(defaultThreshold);
                    })
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        }

        if (!flaggedTopics.isEmpty()) {
            throw new ModerationException(UNSAFE_CONTENT_FOUND, flaggedTopics, prompt);
        }
    }

    private void handleFlags(final List<String> flaggedTopics, final EventData eventData) {

        LOGGER.warn("Unsafe content detected in a message. -> {}", flaggedTopics);
        final TextChannel channel = eventData.getCurrentChannel()
                .asTextChannel();

        final Message message = channel.retrieveMessageById(eventData.getMessage()
                .getId())
                .complete();

        final StringBuilder flaggedMessage = new StringBuilder().append(FLAGGED_MESSAGE);
        if (flaggedTopics != null) {
            final String flaggedTopicsString = flaggedTopics.stream()
                    .collect(Collectors.joining(", "));

            flaggedMessage.append(
                    MessageFormat.format(FLAGGED_TOPICS_MESSAGE, message.getContentDisplay(), flaggedTopicsString));
        }
        message.delete()
                .complete();

        eventData.getCurrentChannel()
                .sendMessage(flaggedMessage.toString())
                .queue(msg -> msg.delete()
                        .queueAfter(5, TimeUnit.SECONDS));
    }

    private void handleFlags(final List<String> flaggedTopics, final ModalInteractionEvent event,
            final String content) {

        LOGGER.warn("Unsafe content detected in a lorebook entry. -> {}", flaggedTopics);
        String flaggedMessage = FLAGGED_ENTRY;

        if (flaggedTopics != null) {
            final String flaggedTopicsString = flaggedTopics.stream()
                    .collect(Collectors.joining(", "));
            flaggedMessage += MessageFormat.format(FLAGGED_TOPICS_LOREBOOK, flaggedTopicsString, content);
        }

        event.reply(flaggedMessage)
                .setEphemeral(true)
                .complete();
    }

    public void handleFlagsOutput(final List<String> flaggedTopics, final EventData eventData, final String content) {

        LOGGER.warn("Unsafe content detected in AI's output. Topics -> {}. Content -> {}", flaggedTopics, content);
        String flaggedMessage = FLAGGED_OUTPUT;

        if (flaggedTopics != null) {
            final String flaggedTopicsString = flaggedTopics.stream()
                    .collect(Collectors.joining(", "));
            flaggedMessage += MessageFormat.format(FLAGGED_TOPICS_OUTPUT, flaggedTopicsString);
        }

        eventData.getCurrentChannel()
                .sendMessage(flaggedMessage)
                .queue(msg -> msg.delete()
                        .queueAfter(5, TimeUnit.SECONDS));
    }
}
