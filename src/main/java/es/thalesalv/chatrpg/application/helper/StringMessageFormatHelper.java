package es.thalesalv.chatrpg.application.helper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.application.util.StringProcessor;
import es.thalesalv.chatrpg.domain.model.EventData;
import es.thalesalv.chatrpg.domain.model.bot.Bump;
import es.thalesalv.chatrpg.domain.model.bot.Nudge;
import es.thalesalv.chatrpg.domain.model.bot.Persona;

@Component
public class StringMessageFormatHelper implements MessageHelper<String>, PersonaHelper<String> {

    @Override
    public List<String> formatMessages(final List<String> messages, final EventData eventData,
            final StringProcessor inputProcessor) {

        final Persona persona = eventData.getChannelDefinitions()
                .getChannelConfig()
                .getPersona();

        final String personality = inputProcessor.process(persona.getPersonality());
        messages.add(0, personality);

        List<String> chatMessages = messages.stream()
                .filter(msg -> !msg.trim()
                        .equals((persona.getName() + SAID).trim()))
                .collect(Collectors.toList());

        chatMessages = formatNudge(persona, chatMessages, inputProcessor);
        return formatBump(persona, chatMessages, inputProcessor);
    }

    @Override
    public List<String> formatNudge(Persona persona, List<String> messages, StringProcessor inputProcessor) {

        return Optional.ofNullable(persona.getNudge())
                .filter(Nudge.isValid)
                .map(ndge -> {
                    messages.add(messages.stream()
                            .filter(m -> !m.startsWith(persona.getName()))
                            .mapToInt(messages::indexOf)
                            .reduce((a, b) -> b)
                            .orElse(0) + 1, inputProcessor.process(ndge.content));
                    return messages;
                })
                .orElse(messages);
    }

    @Override
    public List<String> formatBump(Persona persona, List<String> messages, StringProcessor inputProcessor) {

        return Optional.ofNullable(persona.getBump())
                .filter(Bump.isValid)
                .map(bmp -> {
                    for (int index = messages.size() - 1 - bmp.frequency; index > 0; index = index - bmp.frequency) {
                        messages.add(index, inputProcessor.process(bmp.getContent()));
                    }
                    return messages;
                })
                .orElse(messages);
    }
}
