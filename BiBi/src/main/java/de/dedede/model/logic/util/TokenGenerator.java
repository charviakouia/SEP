package de.dedede.model.logic.util;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Random;

import de.dedede.model.data.dtos.TokenDto;

/**
 * Service for generating pseudo-random tokens in the form of {@link TokenDto}s.
 *
 * @author Ivan Charviakou
 */
public final class TokenGenerator {

	private static final Random RANDOM = new SecureRandom();
	private static final int BYTE_LENGTH = 30;
	private static final int STR_LENGTH = 20;
	private static final int TIME_INTERVAL_LENGTH = 5;
	
	private TokenGenerator() {}

	/**
	 * Constructs a new token and corresponding {@link TokenDto}. The creation time is set to 5 minutes after
	 * the execution of this method to allow for the time before being inserted into the datastore.
	 */
	public static TokenDto generateToken() {
		TokenDto result = new TokenDto();
		byte[] bytes = new byte[BYTE_LENGTH];
	    RANDOM.nextBytes(bytes);
		result.setContent(Base64.getEncoder().withoutPadding().encodeToString(bytes).substring(0, STR_LENGTH));
		result.setCreationTime(LocalDateTime.now().plus(TIME_INTERVAL_LENGTH, ChronoUnit.MINUTES));
		return result;
	}
}
