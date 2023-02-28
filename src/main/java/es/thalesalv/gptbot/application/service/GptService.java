package es.thalesalv.gptbot.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import es.thalesalv.gptbot.adapters.rest.OpenAIApiService;
import es.thalesalv.gptbot.application.translator.GptRequestTranslator;
import es.thalesalv.gptbot.domain.model.gpt.GptRequestEntity;
import es.thalesalv.gptbot.domain.model.gpt.GptResponseEntity;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GptService {

    private final GptRequestTranslator gptRequestTranslator;
    private final OpenAIApiService openAiService;

    private static final String MODEL_CURIE = "text-curie-001";
    private static final String MODEL_BABBAGE = "text-babbage-001";
    private static final String MODEL_ADA = "text-ada-001";
    private static final String MODEL_DAVINCI = "text-davinci-003";
    private static final Logger LOGGER = LoggerFactory.getLogger(GptService.class);

    public Mono<GptResponseEntity> callModel(final String prompt, final String model) {

        LOGGER.debug("Sending prompt to model. Model -> {}, Prompt -> {}", model, prompt);
        final GptRequestEntity request = gptRequestTranslator.buildRequest(prompt, model);
        return openAiService.callGptApi(request);
    }

    public Mono<GptResponseEntity> callDaVinci(final String prompt) {

        LOGGER.debug("Called inference with Davinci");
        return callModel(prompt, MODEL_DAVINCI);
    }

    public Mono<GptResponseEntity> callAda(final String prompt) {

        LOGGER.debug("Called inference with Ada");
        return callModel(prompt, MODEL_ADA);
    }

    public Mono<GptResponseEntity> callCurie(final String prompt) {

        LOGGER.debug("Called inference with Curie");
        return callModel(prompt, MODEL_CURIE);
    }

    public Mono<GptResponseEntity> callBabbage(final String prompt) {

        LOGGER.debug("Called inference with Babbage");
        return callModel(prompt, MODEL_BABBAGE);
    }
}
