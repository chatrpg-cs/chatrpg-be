package es.thalesalv.chatrpg.core.application.usecase.world.result;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class CreateWorldLorebookEntryResult {

    private final String id;

    public static CreateWorldLorebookEntryResult build(String id) {

        return new CreateWorldLorebookEntryResult(id);
    }
}
