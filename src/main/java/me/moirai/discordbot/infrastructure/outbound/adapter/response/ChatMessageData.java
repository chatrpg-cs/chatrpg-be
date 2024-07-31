package me.moirai.discordbot.infrastructure.outbound.adapter.response;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;

public final class ChatMessageData {

    private final String id;
    private final String authorId;
    private final String channelId;
    private final String authorNickname;
    private final String authorUsername;
    private final String content;
    private final List<String> mentionedUsersIds;

    public ChatMessageData(Builder builder) {

        this.id = builder.id;
        this.authorId = builder.authorId;
        this.channelId = builder.channelId;
        this.authorNickname = builder.authorNickname;
        this.authorUsername = builder.authorUsername;
        this.content = builder.content;
        this.mentionedUsersIds = unmodifiableList(builder.mentionedUsersIds);
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getAuthorNickname() {
        return authorNickname;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public String getContent() {
        return content;
    }

    public List<String> getMentionedUsersIds() {
        return mentionedUsersIds;
    }

    public static final class Builder {

        private String id;
        private String authorId;
        private String channelId;
        private String authorNickname;
        private String authorUsername;
        private String content;
        private List<String> mentionedUsersIds = new ArrayList<>();

        private Builder() {
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder authorId(String authorId) {
            this.authorId = authorId;
            return this;
        }

        public Builder channelId(String channelId) {
            this.channelId = channelId;
            return this;
        }

        public Builder authorNickname(String authorNickname) {
            this.authorNickname = authorNickname;
            return this;
        }

        public Builder authorUsername(String authorUsername) {
            this.authorUsername = authorUsername;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder mentionedUsersIds(List<String> mentionedUsersIds) {

            if (mentionedUsersIds != null) {
                this.mentionedUsersIds.addAll(mentionedUsersIds);
            }

            return this;
        }

        public ChatMessageData build() {
            return new ChatMessageData(this);
        }
    }
}
