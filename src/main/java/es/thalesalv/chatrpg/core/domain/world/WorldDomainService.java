package es.thalesalv.chatrpg.core.domain.world;

import es.thalesalv.chatrpg.core.application.command.world.CreateWorld;
import es.thalesalv.chatrpg.core.application.command.world.CreateWorldLorebookEntry;
import es.thalesalv.chatrpg.core.application.command.world.DeleteWorld;
import es.thalesalv.chatrpg.core.application.command.world.DeleteWorldLorebookEntry;
import es.thalesalv.chatrpg.core.application.command.world.UpdateWorld;
import es.thalesalv.chatrpg.core.application.command.world.UpdateWorldLorebookEntry;
import es.thalesalv.chatrpg.core.application.query.world.GetWorldById;
import es.thalesalv.chatrpg.core.application.query.world.GetWorldLorebookEntryById;

public interface WorldDomainService {

    World getWorldById(GetWorldById query);

    World createFrom(CreateWorld command);

    World update(UpdateWorld command);

    void deleteWorld(DeleteWorld command);

    WorldLorebookEntry createLorebookEntry(CreateWorldLorebookEntry command);

    WorldLorebookEntry updateLorebookEntry(UpdateWorldLorebookEntry command);

    WorldLorebookEntry findWorldLorebookEntryById(GetWorldLorebookEntryById query);

    void deleteLorebookEntry(DeleteWorldLorebookEntry command);
}
