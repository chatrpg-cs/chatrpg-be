package es.thalesalv.chatrpg.adapters.rest.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.thalesalv.chatrpg.application.service.WorldService;
import es.thalesalv.chatrpg.domain.exception.InsufficientPermissionException;
import es.thalesalv.chatrpg.domain.exception.LorebookEntryNotFoundException;
import es.thalesalv.chatrpg.domain.exception.WorldNotFoundException;
import es.thalesalv.chatrpg.domain.model.api.ApiErrorResponse;
import es.thalesalv.chatrpg.domain.model.api.ApiResponse;
import es.thalesalv.chatrpg.domain.model.bot.LorebookEntry;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lore/entry")
public class LorebookEntryController {

    private final WorldService worldService;

    private static final String RETRIEVE_ALL_LOREBOOKS_IN_LOREBOOK_REQUEST = "Received request for listing all lorebookEntries in loreboko with id {}";
    private static final String SAVE_LOREBOOK_ENTRY_REQUEST = "Received request for saving lorebookEntry -> {}. lorebookId -> {}";
    private static final String UPDATE_LOREBOOK_ENTRY_REQUEST = "Received request for updating lorebookEntry with ID {} -> {}";
    private static final String DELETE_LOREBOOK_ENTRY_REQUEST = "Received request for deleting lorebookEntry with ID {}";
    private static final String GENERAL_ERROR_MESSAGE = "An error occurred processing the request";
    private static final String NOT_ENOUGH_PERMISSION = "Not enough permissions to modify this world";
    private static final String LOREBOOK_NOT_FOUND = "The requested world for modification does not exist";
    private static final String LOREBOOK_ENTRY_NOT_FOUND = "The requested entry for modification does not exist";

    private static final Logger LOGGER = LoggerFactory.getLogger(LorebookEntryController.class);

    @GetMapping("lorebook/{lorebook-id}")
    public Mono<ResponseEntity<ApiResponse>> getAllLorebookEntriesFromLorebook(
            @RequestHeader("requester") String requesterUserId,
            @PathVariable(value = "lorebook-id") final String lorebookId) {

        try {
            LOGGER.info(RETRIEVE_ALL_LOREBOOKS_IN_LOREBOOK_REQUEST, lorebookId);
            final List<LorebookEntry> lorebookEntries = worldService.retrieveAllLorebookEntriesInLorebook(lorebookId,
                    requesterUserId);

            return Mono.just(buildResponse(lorebookEntries));
        } catch (Exception e) {
            LOGGER.error("Error retrieving all lorebookEntries from lorebook", e);
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(this.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, GENERAL_ERROR_MESSAGE)));
        }
    }

    @PostMapping("{lorebook-id}")
    public Mono<ResponseEntity<ApiResponse>> saveLorebookEntry(@RequestHeader("requester") String requesterUserId,
            @PathVariable(value = "lorebook-id") final String lorebookId,
            @RequestBody final LorebookEntry lorebookEntry) {

        try {
            LOGGER.info(SAVE_LOREBOOK_ENTRY_REQUEST, lorebookEntry, lorebookId);
            final LorebookEntry createdLorebookEntry = worldService.saveLorebookEntry(lorebookEntry, lorebookId,
                    requesterUserId);

            return Mono.just(buildResponse(createdLorebookEntry));
        } catch (InsufficientPermissionException e) {
            LOGGER.error(NOT_ENOUGH_PERMISSION, e);
            return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN.value())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(this.buildErrorResponse(HttpStatus.FORBIDDEN, NOT_ENOUGH_PERMISSION)));
        } catch (WorldNotFoundException e) {
            LOGGER.error(LOREBOOK_NOT_FOUND, e);
            return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND.value())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(this.buildErrorResponse(HttpStatus.NOT_FOUND, LOREBOOK_NOT_FOUND)));
        } catch (Exception e) {
            LOGGER.error(GENERAL_ERROR_MESSAGE, e);
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(this.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, GENERAL_ERROR_MESSAGE)));
        }
    }

    @PutMapping("{lorebook-entry-id}")
    public Mono<ResponseEntity<ApiResponse>> updateLorebookEntry(@RequestHeader("requester") String requesterUserId,
            @PathVariable(value = "lorebook-entry-id") final String lorebookEntryId,
            @RequestBody final LorebookEntry lorebookEntry) {

        try {
            LOGGER.info(UPDATE_LOREBOOK_ENTRY_REQUEST, lorebookEntryId, lorebookEntry);
            final LorebookEntry updatedLorebookEntry = worldService.updateLorebookEntry(lorebookEntryId, lorebookEntry,
                    requesterUserId);

            return Mono.just(buildResponse(updatedLorebookEntry));
        } catch (InsufficientPermissionException e) {
            LOGGER.error(NOT_ENOUGH_PERMISSION, e);
            return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN.value())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(this.buildErrorResponse(HttpStatus.FORBIDDEN, NOT_ENOUGH_PERMISSION)));
        } catch (LorebookEntryNotFoundException e) {
            LOGGER.error(LOREBOOK_ENTRY_NOT_FOUND, e);
            return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND.value())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(this.buildErrorResponse(HttpStatus.NOT_FOUND, LOREBOOK_ENTRY_NOT_FOUND)));
        } catch (Exception e) {
            LOGGER.error(GENERAL_ERROR_MESSAGE, e);
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(this.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, GENERAL_ERROR_MESSAGE)));
        }
    }

    @DeleteMapping("{lorebook-entry-id}")
    public Mono<ResponseEntity<ApiResponse>> deleteLorebookEntry(@RequestHeader("requester") String requesterUserId,
            @PathVariable(value = "lorebook-entry-id") final String lorebookEntryId) {

        try {
            LOGGER.info(DELETE_LOREBOOK_ENTRY_REQUEST, lorebookEntryId);
            worldService.deleteLorebookEntry(lorebookEntryId, requesterUserId);
            return Mono.just(ResponseEntity.ok()
                    .body(ApiResponse.empty()));
        } catch (InsufficientPermissionException e) {
            LOGGER.error(NOT_ENOUGH_PERMISSION, e);
            return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN.value())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(this.buildErrorResponse(HttpStatus.FORBIDDEN, NOT_ENOUGH_PERMISSION)));
        } catch (LorebookEntryNotFoundException e) {
            LOGGER.error(LOREBOOK_ENTRY_NOT_FOUND, e);
            return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND.value())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(this.buildErrorResponse(HttpStatus.NOT_FOUND, LOREBOOK_ENTRY_NOT_FOUND)));
        } catch (Exception e) {
            LOGGER.error(GENERAL_ERROR_MESSAGE, e);
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(this.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, GENERAL_ERROR_MESSAGE)));
        }
    }

    private ResponseEntity<ApiResponse> buildResponse(List<LorebookEntry> lorebookEntries) {

        LOGGER.info("Sending response for lorebookEntries -> {}", lorebookEntries);
        final ApiResponse respose = ApiResponse.builder()
                .lorebookEntries(lorebookEntries)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(respose);
    }

    private ResponseEntity<ApiResponse> buildResponse(LorebookEntry lorebookEntry) {

        LOGGER.info("Sending response for lorebookEntries -> {}", lorebookEntry);
        final ApiResponse respose = ApiResponse.builder()
                .lorebookEntry(lorebookEntry)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(respose);
    }

    private ApiResponse buildErrorResponse(HttpStatus status, String message) {

        LOGGER.debug("Building error response object for lorebookEntries");
        return ApiResponse.builder()
                .error(ApiErrorResponse.builder()
                        .message(message)
                        .status(status)
                        .build())
                .build();
    }
}
