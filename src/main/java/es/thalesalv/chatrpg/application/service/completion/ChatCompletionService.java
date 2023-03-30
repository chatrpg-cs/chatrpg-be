package es.thalesalv.chatrpg.application.service.completion;

import java.util.List;
import java.util.Set;

import es.thalesalv.chatrpg.application.util.StringProcessors;
import es.thalesalv.chatrpg.domain.enums.Intent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.adapters.rest.OpenAIApiService;
import es.thalesalv.chatrpg.application.errorhandling.CommonErrorHandler;
import es.thalesalv.chatrpg.application.helper.MessageFormatHelper;
import es.thalesalv.chatrpg.application.mapper.airequest.ChatCompletionRequestMapper;
import es.thalesalv.chatrpg.application.util.StringProcessor;
import es.thalesalv.chatrpg.domain.exception.ModelResponseBlankException;
import es.thalesalv.chatrpg.domain.model.EventData;
import es.thalesalv.chatrpg.domain.model.chconf.ChannelConfig;
import es.thalesalv.chatrpg.domain.model.chconf.LorebookEntry;
import es.thalesalv.chatrpg.domain.model.chconf.Persona;
import es.thalesalv.chatrpg.domain.model.chconf.World;
import es.thalesalv.chatrpg.domain.model.openai.completion.ChatCompletionRequest;
import es.thalesalv.chatrpg.domain.model.openai.completion.ChatMessage;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.User;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ChatCompletionService implements CompletionService {

    private final MessageFormatHelper messageFormatHelper;
    private final CommonErrorHandler commonErrorHandler;
    private final ChatCompletionRequestMapper chatCompletionsRequestTranslator;
    private final OpenAIApiService openAiService;
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatCompletionService.class);

    @Override
    public Mono<String> generate(final List<String> messages, final EventData eventData) {

        LOGGER.debug("Called inference for Chat Completions.");
        final StringProcessor outputProcessor = new StringProcessor();
        final StringProcessor inputProcessor = new StringProcessor();
        final Mentions mentions = eventData.getMessage()
                .getMentions();
        final User author = eventData.getMessageAuthor();
        final ChannelConfig channelConfig = eventData.getChannelDefinitions()
                .getChannelConfig();
        final World world = channelConfig.getWorld();
        final Persona persona = channelConfig.getPersona();
        inputProcessor.addRule(StringProcessors.replacePlaceholderWithPersona(persona));
        inputProcessor.addRule(StringProcessors.replaceRegex(eventData.getBot()
                .getName(), persona.getName()));
        outputProcessor.addRule(StringProcessors.stripAsNamePrefixForUppercase(persona.getName()));
        outputProcessor.addRule(StringProcessors.stripAsNamePrefixForLowercase(persona.getName()));
        outputProcessor.addRule(StringProcessors.stripTrailingFragment());
        final Set<LorebookEntry> entriesFound = messageFormatHelper.handleEntriesMentioned(messages, world);
        switch (persona.getIntent()) {
            case RPG -> {
                messageFormatHelper.handlePlayerCharacterEntries(entriesFound, messages, author, mentions, world);
                messageFormatHelper.processEntriesFoundForRpg(entriesFound, messages, author.getJDA());
            }
            case CHAT -> messageFormatHelper.processEntriesFoundForChat(entriesFound, messages);
            case AUTHOR -> messageFormatHelper.processEntriesFoundForAuthor(entriesFound, messages);
        }
        final List<ChatMessage> chatMessages = messageFormatHelper.formatMessagesForChatCompletions(messages, eventData,
                inputProcessor);
        if (Intent.AUTHOR.equals(persona.getIntent())) {
            chatMessages.forEach(m -> m.setContent(StringProcessors.stripChatPrefix()
                    .apply(m.getContent())));
        }
        final ChatCompletionRequest request = chatCompletionsRequestTranslator.buildRequest(chatMessages,
                eventData.getChannelDefinitions()
                        .getChannelConfig());
        return openAiService.callGptChatApi(request, eventData)
                .map(response -> {
                    final String responseText = response.getChoices()
                            .get(0)
                            .getMessage()
                            .getContent();
                    if (StringUtils.isBlank(responseText)) {
                        throw new ModelResponseBlankException();
                    }
                    return outputProcessor.process(responseText.trim());
                })
                .doOnError(ModelResponseBlankException.class::isInstance,
                        e -> commonErrorHandler.handleEmptyResponse(eventData));
    }
}
