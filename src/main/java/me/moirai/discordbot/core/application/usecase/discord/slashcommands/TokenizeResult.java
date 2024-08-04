package me.moirai.discordbot.core.application.usecase.discord.slashcommands;

public final class TokenizeResult {

    private final String tokens;
    private final long[] tokenIds;
    private final int tokenCount;
    private final int characterCount;

    private TokenizeResult(Builder builder) {
        this.tokens = builder.tokens;
        this.tokenIds = builder.tokenIds;
        this.tokenCount = builder.tokenCount;
        this.characterCount = builder.characterCount;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getTokens() {
        return tokens;
    }

    public long[] getTokenIds() {
        return tokenIds;
    }

    public int getTokenCount() {
        return tokenCount;
    }

    public int getCharacterCount() {
        return characterCount;
    }

    public static final class Builder {

        private String tokens;
        private long[] tokenIds;
        private int tokenCount;
        private int characterCount;

        public Builder tokens(String tokens) {
            this.tokens = tokens;
            return this;
        }

        public Builder tokenIds(long[] tokenIds) {
            this.tokenIds = tokenIds;
            return this;
        }

        public Builder tokenCount(int tokenCount) {
            this.tokenCount = tokenCount;
            return this;
        }

        public Builder characterCount(int characterCount) {
            this.characterCount = characterCount;
            return this;
        }

        public TokenizeResult build() {
            return new TokenizeResult(this);
        }
    }
}
