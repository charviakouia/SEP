package de.dedede.model.logic.util;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Random;

import de.dedede.model.data.dtos.TokenDto;

/**
 * Service for generating this token in {@link TokenDto}.
 */
public final class TokenGenerator {

	private static final Random RANDOM = new SecureRandom();
	private static final int BYTE_LENGTH = 30;
	private static final int STR_LENGTH = 20;
	
	private TokenGenerator() {}

	/**
	 * Constructs a new token.
	 */
	public static TokenDto generateToken() {
		TokenDto result = new TokenDto();
		byte[] bytes = new byte[BYTE_LENGTH];
	    RANDOM.nextBytes(bytes);
		result.setContent(Base64.getEncoder().withoutPadding().encodeToString(bytes).substring(0, STR_LENGTH));
		result.setCreationTime(LocalDateTime.now().plus(5, ChronoUnit.MINUTES));
		return result;
	}
}
