package es.thalesalv.chatrpg.application.service.completion;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.adapters.rest.client.CompletionApiService;
import es.thalesalv.chatrpg.application.errorhandling.CommonErrorHandler;
import es.thalesalv.chatrpg.application.helper.LorebookHelper;
import es.thalesalv.chatrpg.application.helper.MessageHelper;
import es.thalesalv.chatrpg.application.mapper.airequest.TextCompletionRequestMapper;
import es.thalesalv.chatrpg.application.util.StringProcessor;
import es.thalesalv.chatrpg.domain.enums.Intent;
import es.thalesalv.chatrpg.domain.exception.ModelResponseBlankException;
import es.thalesalv.chatrpg.domain.model.EventData;
import es.thalesalv.chatrpg.domain.model.bot.ChannelConfig;
import es.thalesalv.chatrpg.domain.model.bot.LorebookEntry;
import es.thalesalv.chatrpg.domain.model.bot.Persona;
import es.thalesalv.chatrpg.domain.model.bot.World;
import es.thalesalv.chatrpg.domain.model.openai.completion.TextCompletionRequest;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.User;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TextCompletionService implements CompletionService {

    private final LorebookHelper lorebookHelper;
    private final MessageHelper<String> messageHelper;
    private final CommonErrorHandler commonErrorHandler;
    private final TextCompletionRequestMapper textCompletionRequestTranslator;
    private final CompletionApiService<TextCompletionRequest> completionApiService;

    private static final Logger LOGGER = LoggerFactory.getLogger(TextCompletionService.class);

    @Override
    public Mono<String> generate(List<String> messages, EventData eventData) {

        LOGGER.debug("Called inference for Text Completions.");
        final StringProcessor outputProcessor = new StringProcessor();
        final StringProcessor inputProcessor = new StringProcessor();
        final Mentions mentions = eventData.getMessage()
                .getMentions();

        final User author = eventData.getMessageAuthor();
        final ChannelConfig channelConfig = eventData.getChannelDefinitions()
                .getChannelConfig();

        final World world = channelConfig.getWorld();
        final Persona persona = channelConfig.getPersona();
        inputProcessor.addRule(s -> Pattern.compile("\\{0\\}")
                .matcher(s)
                .replaceAll(r -> persona.getName()));

        inputProcessor.addRule(s -> Pattern.compile(eventData.getBot()
                .getName())
                .matcher(s)
                .replaceAll(r -> persona.getName()));

        outputProcessor.addRule(s -> Pattern.compile("\\bAs " + persona.getName() + ", (\\w)")
                .matcher(s)
                .replaceAll(r -> r.group(1)
                        .toUpperCase()));

        outputProcessor.addRule(s -> Pattern.compile("\\bas " + persona.getName() + ", (\\w)")
                .matcher(s)
                .replaceAll(r -> r.group(1)));

        outputProcessor.addRule(
                s -> Pattern.compile("(?<=[.!?\\n])\"?[^.!?\\n]*(?![.!?\\n])$", Pattern.DOTALL & Pattern.MULTILINE)
                        .matcher(s)
                        .replaceAll(StringUtils.EMPTY));

        List<String> processedMessages = messages;
        final List<LorebookEntry> entriesFound = lorebookHelper.handleEntriesMentioned(processedMessages, world);
        if (Intent.RPG.equals(persona.getIntent())) {
            processedMessages = lorebookHelper.handlePlayerCharacterEntries(entriesFound, processedMessages, author,
                    mentions, world);
            processedMessages = lorebookHelper.rpgModeLorebookEntries(entriesFound, processedMessages, author.getJDA());
        } else {
            processedMessages = lorebookHelper.chatModeLorebookEntries(entriesFound, processedMessages);
        }

        final List<String> chatMessages = messageHelper.formatMessages(processedMessages, eventData, inputProcessor);
        final String chatifiedMessage = messageHelper.chatifyMessages(chatMessages, eventData, inputProcessor);
        final TextCompletionRequest request = textCompletionRequestTranslator.buildRequest(chatifiedMessage,
                eventData.getChannelDefinitions()
                        .getChannelConfig());

        return completionApiService.callCompletion(request, eventData)
                .map(response -> {
                    final String responseText = response.getChoices()
                            .get(0)
                            .getText();
                    if (StringUtils.isBlank(responseText)) {
                        throw new ModelResponseBlankException();
                    }
                    return outputProcessor.process(responseText.trim());
                })
                .doOnError(ModelResponseBlankException.class::isInstance,
                        e -> commonErrorHandler.handleEmptyResponse(eventData));
    }
}
