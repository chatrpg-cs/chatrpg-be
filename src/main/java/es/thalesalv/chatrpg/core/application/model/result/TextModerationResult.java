package es.thalesalv.chatrpg.core.application.model.result;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TextModerationResult {

    private final boolean contentFlagged;
    private final Map<String, Double> moderationScores;
    private final List<String> flaggedTopics;

    public TextModerationResult(Builder builder) {
        this.contentFlagged = builder.contentFlagged;

        this.moderationScores = unmodifiableMap(
                builder.moderationScores == null ? emptyMap() : new HashMap<>(builder.moderationScores));

        this.flaggedTopics = unmodifiableList(
                builder.flaggedTopics == null ? emptyList() : new ArrayList<>(builder.flaggedTopics));
    }

    public static Builder builder() {

        return new Builder();
    }

    public boolean isContentFlagged() {
        return contentFlagged;
    }

    public Map<String, Double> getModerationScores() {
        return moderationScores;
    }

    public List<String> getFlaggedTopics() {
        return flaggedTopics;
    }

    public static class Builder {

        private boolean contentFlagged;
        private Map<String, Double> moderationScores;
        private List<String> flaggedTopics;

        public Builder contentFlagged(boolean contentFlagged) {
            this.contentFlagged = contentFlagged;
            return this;
        }

        public Builder moderationScores(Map<String, Double> moderationScores) {
            this.moderationScores = moderationScores;
            return this;
        }

        public Builder flaggedTopics(List<String> flaggedTopics) {
            this.flaggedTopics = flaggedTopics;
            return this;
        }

        public TextModerationResult build() {

            return new TextModerationResult(this);
        }
    }
}
