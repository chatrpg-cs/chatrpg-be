package me.moirai.discordbot.common.dbutil;

import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

@Component
public class NanoId {

    private static String alphabet;
    private static int idLength;

    @Value("${moirai.nano-id.alphabet}")
    public void setAlphabet(final String alphabet) {

        NanoId.alphabet = alphabet;
    }

    @Value("${moirai.nano-id.characters-amount}")
    public void setAlphabet(final int idLength) {

        NanoId.idLength = idLength;
    }

    public static String randomNanoId() {

        return NanoIdUtils.randomNanoId(new SecureRandom(), alphabet.toCharArray(), idLength);
    }
}
