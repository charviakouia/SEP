package de.dedede.model.logic.util;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Random;

import de.dedede.model.data.dtos.TokenDto;

/**
 * Service for generating this token in {@link TokenDto}.
 */
public final class TokenGenerator {

	private static final Random RANDOM = new SecureRandom();
	private static final int LENGTH = 10;
	
	private TokenGenerator() {}

	/**
	 * Constructs a new token.
	 */
	public static TokenDto generateToken() {
		TokenDto result = new TokenDto();
		byte[] bytes = new byte[LENGTH];
	    RANDOM.nextBytes(bytes);
		result.setContent(String.valueOf(bytes));
		result.setCreationTime(LocalDateTime.now());
		return result;
	}
}
