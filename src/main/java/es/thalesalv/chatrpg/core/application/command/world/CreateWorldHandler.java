package es.thalesalv.chatrpg.core.application.command.world;

import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.common.cqrs.command.CommandHandler;
import es.thalesalv.chatrpg.core.domain.world.World;
import es.thalesalv.chatrpg.core.domain.world.WorldDomainService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreateWorldHandler extends CommandHandler<CreateWorld, CreateWorldResult> {

    private final WorldDomainService domainService;

    @Override
    public CreateWorldResult handle(CreateWorld command) {

        World world = domainService.createFrom(command);
        return CreateWorldResult.with(world.getId());
    }
}