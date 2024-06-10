package es.thalesalv.chatrpg.common.exception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Getter;

@Getter
public class ModerationException extends RuntimeException {

    private final List<String> flaggedTopics;
    private final String channelId;

    public ModerationException(String message, String channelId, List<String> flaggedTopics) {

        super(message);

        this.flaggedTopics = Collections.unmodifiableList(new ArrayList<>(flaggedTopics));
        this.channelId = channelId;
    }
}
