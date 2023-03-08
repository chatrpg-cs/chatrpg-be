package es.thalesalv.gptbot.adapters.data;

import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Component;

import es.thalesalv.gptbot.application.config.CommandEventData;

@Component
public class ContextDatastore {

    private ThreadLocal<CommandEventData> commandEventData = new ThreadLocal<>();

    public void setCommandEventData(final CommandEventData commandEventData) {

        this.commandEventData.set(commandEventData);
    }

    public CommandEventData getCommandEventData() {

        return Optional.ofNullable(this.commandEventData)
        		.map(ThreadLocal::get)
        		.orElseThrow(()-> new NullPointerException("commandEventData not set on thread"));
    }

    public boolean isCommandEventDataNull() {

        return Objects.isNull(commandEventData.get());
    }

    public void clearContext() {

        this.commandEventData.remove();
    }
}
