package es.thalesalv.chatrpg.domain.model.openai.moderation;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import es.thalesalv.chatrpg.application.util.json.JsonMapDoubleDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModerationResult {

    @JsonProperty("categories")
    private Map<String, Boolean> categories;

    @JsonProperty("category_scores")
    @JsonDeserialize(using = JsonMapDoubleDeserializer.class)
    private Map<String, String> categoryScores;

    @JsonProperty("flagged")
    private Boolean flagged;
}
