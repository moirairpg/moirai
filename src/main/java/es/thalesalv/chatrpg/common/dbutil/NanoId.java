package es.thalesalv.chatrpg.common.dbutil;

import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

@Component
public class NanoId {

    private static String ALPHABET;
    private static int ID_LENGTH;

    @Value("${chatrpg.nano-id.alphabet}")
    public void setAlphabet(final String alphabet) {

        NanoId.ALPHABET = alphabet;
    }

    @Value("${chatrpg.nano-id.characters-amount}")
    public void setAlphabet(final int idLength) {

        NanoId.ID_LENGTH = idLength;
    }

    public static String randomNanoId() {

        return NanoIdUtils.randomNanoId(new SecureRandom(), ALPHABET.toCharArray(), ID_LENGTH);
    }
}
