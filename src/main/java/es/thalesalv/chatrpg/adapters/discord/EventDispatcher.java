package es.thalesalv.chatrpg.adapters.discord;

import jakarta.annotation.PostConstruct;
import net.dv8tion.jda.api.JDA;
import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.discord.listener.InteractionListener;
import es.thalesalv.chatrpg.adapters.discord.listener.MessageListener;
import es.thalesalv.chatrpg.adapters.discord.listener.SessionListener;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.session.SessionDisconnectEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@RequiredArgsConstructor
@Component
public class EventDispatcher extends ListenerAdapter {

    private final SessionListener sessionListener;
    private final InteractionListener interactionListener;
    private final MessageListener messageListener;
    private final JDA jda;

    @PostConstruct
    public void init() {

        jda.addEventListener(this);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        messageListener.onMessageReceived(event);
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        interactionListener.onSlashCommand(event);
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {

        interactionListener.onModalInteraction(event);
    }

    @Override
    public void onReady(ReadyEvent event) {

        sessionListener.onReady(event);
    }

    @Override
    public void onSessionDisconnect(SessionDisconnectEvent event) {

        sessionListener.onSessionDisconnect(event);
    }

    @Override
    public void onShutdown(ShutdownEvent event) {

        sessionListener.onShutdown(event);
    }
}
