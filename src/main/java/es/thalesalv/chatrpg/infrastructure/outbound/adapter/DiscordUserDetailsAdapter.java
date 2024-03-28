package es.thalesalv.chatrpg.infrastructure.outbound.adapter;

import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import es.thalesalv.chatrpg.common.exception.DiscordApiException;
import es.thalesalv.chatrpg.core.application.port.DiscordUserDetailsPort;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.DiscordErrorResponse;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.DiscordUserDataResponse;
import reactor.core.publisher.Mono;

@Component
public class DiscordUserDetailsAdapter implements DiscordUserDetailsPort {

    private static final String USERS_BASE_URI = "/users/%s";
    private static final String AUTHENTICATION_ERROR = "Error authenticating user on Discord";
    private static final String UNKNOWN_ERROR = "Error on Discord API";

    private static final Predicate<HttpStatusCode> BAD_REQUEST = statusCode -> statusCode
            .isSameCodeAs(HttpStatusCode.valueOf(400));

    private static final Predicate<HttpStatusCode> UNAUTHORIZED = statusCode -> statusCode
            .isSameCodeAs(HttpStatusCode.valueOf(401));

    private final WebClient webClient;

    public DiscordUserDetailsAdapter(@Value("${chatrpg.discord.api-base-url}") String discordBaseUrl,
            WebClient.Builder webClientBuilder) {

        this.webClient = webClientBuilder.baseUrl(discordBaseUrl).build();
    }

    @Override
    public Mono<DiscordUserDataResponse> retrieveLoggedUser(String token) {

        return getDiscordUserWebClient(String.format(USERS_BASE_URI, "@me"), token);
    }

    @Override
    public Mono<DiscordUserDataResponse> retrieveUserById(String token, String discordUserId) {

        return getDiscordUserWebClient(String.format(USERS_BASE_URI, discordUserId), token);
    }

    private Mono<DiscordUserDataResponse> getDiscordUserWebClient(String uri, String token) {

        return webClient.get()
                .uri(uri)
                .headers(headers -> {
                    headers.add(HttpHeaders.AUTHORIZATION, token);
                    headers.add(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
                })
                .retrieve()
                .onStatus(UNAUTHORIZED, this::handleUnauthorized)
                .onStatus(BAD_REQUEST, this::handleBadRequest)
                .onStatus(HttpStatusCode::isError, this::handleUnknownError)
                .bodyToMono(DiscordUserDataResponse.class);
    }

    private Mono<? extends Throwable> handleUnauthorized(ClientResponse clientResponse) {

        return Mono.error(new DiscordApiException(HttpStatus.UNAUTHORIZED, AUTHENTICATION_ERROR));
    }

    private Mono<? extends Throwable> handleBadRequest(ClientResponse clientResponse) {

        return clientResponse.bodyToMono(DiscordErrorResponse.class)
                .map(resp -> new DiscordApiException(HttpStatus.BAD_REQUEST, resp.getError(),
                        resp.getErrorDescription(),
                        String.format(AUTHENTICATION_ERROR, resp.getError(), resp.getErrorDescription())));
    }

    private Mono<? extends Throwable> handleUnknownError(ClientResponse clientResponse) {

        return clientResponse.bodyToMono(DiscordErrorResponse.class)
                .map(resp -> new DiscordApiException(HttpStatus.INTERNAL_SERVER_ERROR, resp.getError(),
                        resp.getErrorDescription(),
                        String.format(UNKNOWN_ERROR, resp.getError(), resp.getErrorDescription())));
    }
}