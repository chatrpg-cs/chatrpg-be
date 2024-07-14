package es.thalesalv.chatrpg.core.application.usecase.world;

import org.apache.commons.lang3.StringUtils;

import es.thalesalv.chatrpg.common.annotation.UseCaseHandler;
import es.thalesalv.chatrpg.common.usecases.AbstractUseCaseHandler;
import es.thalesalv.chatrpg.core.application.usecase.world.request.DeleteWorldLorebookEntry;
import es.thalesalv.chatrpg.core.domain.world.WorldService;

@UseCaseHandler
public class DeleteWorldLorebookEntryHandler extends AbstractUseCaseHandler<DeleteWorldLorebookEntry, Void> {

    private static final String ENTRY_ID_CANNOT_BE_NULL_OR_EMPTY = "Lorebook entry ID cannot be null or empty";
    private static final String WORLD_ID_CANNOT_BE_NULL_OR_EMPTY = "World ID cannot be null or empty";

    private final WorldService domainService;

    public DeleteWorldLorebookEntryHandler(WorldService domainService) {
        this.domainService = domainService;
    }

    @Override
    public void validate(DeleteWorldLorebookEntry command) {

        if (StringUtils.isBlank(command.getLorebookEntryId())) {
            throw new IllegalArgumentException(ENTRY_ID_CANNOT_BE_NULL_OR_EMPTY);
        }

        if (StringUtils.isBlank(command.getWorldId())) {
            throw new IllegalArgumentException(WORLD_ID_CANNOT_BE_NULL_OR_EMPTY);
        }
    }

    @Override
    public Void execute(DeleteWorldLorebookEntry command) {

        domainService.deleteLorebookEntry(command);

        return null;
    }
}
