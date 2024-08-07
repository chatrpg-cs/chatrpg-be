package me.moirai.discordbot.core.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.common.usecases.UseCaseRunner;
import me.moirai.discordbot.common.usecases.UseCaseRunnerImpl;

@Configuration
public class UseCaseConfig {

    private static final Logger LOG = LoggerFactory.getLogger(UseCaseConfig.class);

    private static final String REGISTERED_COMMAND_HANDLERS = "{} use case handlers have been registered";

    @Bean
    public UseCaseRunner seCaseRunner(List<AbstractUseCaseHandler<?, ?>> handlers) {

        UseCaseRunner runner = new UseCaseRunnerImpl();
        handlers.forEach(runner::registerHandler);

        LOG.info(REGISTERED_COMMAND_HANDLERS, handlers.size());

        return runner;
    }
}
