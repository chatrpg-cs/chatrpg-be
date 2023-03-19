package es.thalesalv.chatrpg.application.service.interfaces;

import java.util.List;

import es.thalesalv.chatrpg.domain.model.openai.dto.MessageEventData;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface GptModelService {

    Mono<String> generate(final String prompt, final List<String> messages, final MessageEventData messageEventData);
}
