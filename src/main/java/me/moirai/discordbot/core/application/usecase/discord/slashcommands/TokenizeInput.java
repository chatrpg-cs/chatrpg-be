package me.moirai.discordbot.core.application.usecase.discord.slashcommands;

import java.util.Optional;

import me.moirai.discordbot.common.usecases.UseCase;

public final class TokenizeInput extends UseCase<Optional<TokenizeResult>> {

    private final String input;

    private TokenizeInput(String input) {
        this.input = input;
    }

    public static TokenizeInput build(String input) {
        return new TokenizeInput(input);
    }

    public String getInput() {
        return input;
    }
}
