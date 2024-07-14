package me.moirai.discordbot.core.domain;

import static me.moirai.discordbot.core.domain.Visibility.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import me.moirai.discordbot.common.exception.BusinessRuleViolationException;

public class VisibilityTest {

    @Test
    public void convertFromString() {

        // Given
        String visibilityString = "private";

        // When
        Visibility visibility = Visibility.fromString(visibilityString);

        // Then
        assertThat(visibility).isEqualTo(PRIVATE);
    }

    @Test
    public void errorWhenVisibilityIsInvalid() {

        // Given
        String visibilityString = "ahahahaha";

        // Then
        assertThrows(BusinessRuleViolationException.class, () -> Visibility.fromString(visibilityString));
    }

    @Test
    public void errorWhenVisibilityIsNull() {

        // Given
        String visibilityString = null;

        // Then
        assertThrows(BusinessRuleViolationException.class, () -> Visibility.fromString(visibilityString));
    }
}
