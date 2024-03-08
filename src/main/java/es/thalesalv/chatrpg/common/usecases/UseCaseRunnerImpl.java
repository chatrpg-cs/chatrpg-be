package es.thalesalv.chatrpg.common.usecases;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;

@SuppressWarnings("all")
public class UseCaseRunnerImpl implements UseCaseRunner {

    private static final String HANDLER_NOT_FOUND = "No command handler found for %s";
    private static final String HANDLER_CANNOT_BE_NULL = "Cannot register null handlers";
    private static final String HANDLER_ALREADY_REGISTERED = "Cannot register command handler for %s - there is a handler already registered";
    private static final String HANDLER_REGISTERED = "Handler {} registered for command {}";

    private static final Logger LOGGER = LoggerFactory.getLogger(UseCaseRunnerImpl.class);

    private final Map<Class<? extends UseCase<?>>, UseCaseHandler<?, ?>> handlersByCommand = new HashMap<>();

    @Override
    public <T> T run(UseCase<T> command) {

        UseCaseHandler<?, ?> handler = handlersByCommand.get(command.getClass());

        if (handler == null) {
            String errorMessage = String.format(HANDLER_NOT_FOUND, command.getClass().getSimpleName());
            throw new IllegalArgumentException(errorMessage);
        }

        return ((UseCaseHandler<UseCase<T>, T>) handler).handle(command);
    }

    @Override
    public <A extends UseCase<T>, T> void registerHandler(UseCaseHandler<A, T> handler) {

        if (Objects.isNull(handler)) {
            throw new IllegalArgumentException(HANDLER_CANNOT_BE_NULL);
        }

        Class<A> commandType = extractCommandType(handler);

        boolean isHandlerAlreadyRegisteredForCommand = handlersByCommand.containsKey(commandType);
        if (isHandlerAlreadyRegisteredForCommand) {
            throw new IllegalArgumentException(HANDLER_ALREADY_REGISTERED);
        }

        handlersByCommand.putIfAbsent(commandType, handler);

        LOGGER.info(HANDLER_REGISTERED, handler.getClass().getSimpleName(), commandType.getSimpleName());
    }

    private <A extends UseCase<T>, T> Class<A> extractCommandType(UseCaseHandler<A, T> handler) {

        Class<? extends UseCaseHandler<A, T>> unproxiedHandler =
                (Class<? extends UseCaseHandler<A, T>>) AopUtils.getTargetClass(handler);

        ParameterizedType parameterizedType = (ParameterizedType) unproxiedHandler.getGenericSuperclass();

        return (Class<A>) parameterizedType.getActualTypeArguments()[0];
    }
}
