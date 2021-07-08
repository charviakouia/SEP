package de.dedede.model.logic.util;

import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.persistence.util.Logger;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Hashes the password and salt of the {@link UserDto} with SHA3-256 into hex.
 * Also capsules a generator method for new salts.
 * 
 * @author Jonas Picker
 */
public final class PasswordHashingModule {
	
	/**
	 * The generator used to spew out pseudo-random numbers.
	 */
	private static final Random RANDOM = new SecureRandom();
	
	/**
	 * The desired number of characters the password salt should have, 
	 * divided by 2. 
	 */
	private static final int LENGTH = 20;
	
	/**
	 * The standart character encoding used.
	 */
	private static final Charset UTF_8 = StandardCharsets.UTF_8;

	/**
	 * Hash a password and salt with the SHA3-256 hash function.
	 *
	 * @param password The String which should be hashed.
	 * @param salt The salt the password is concatenated with before hashing
	 * @return password and salt in a 64 sign long hexadecimal form as String.
	 */
	public static String hashPassword(String password, String salt) {
		String hashAsString = null;
		String inputString = password + salt;
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA3-256");
			byte[] InputAsBytes = inputString.getBytes(UTF_8);
			byte[] hashAsBytes = messageDigest.digest(InputAsBytes);
			hashAsString = bytesToHexConverter(hashAsBytes);
		} catch (NoSuchAlgorithmException e) {
			Logger.development("The SHA3-256 algorithm doesn't seem to exist.");
		}

		return hashAsString;
	}
	
	/**
	 * Generates a new random hexadecimal number.
	 * 
	 * @return a String with the number twice the size of @see LENGTH
	 */
	public static String generateSalt(){
	     byte[] result = new byte[LENGTH];
	     RANDOM.nextBytes(result);
	     
	     return bytesToHexConverter(result);
	}
	
	private static String bytesToHexConverter(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        
        return sb.toString();
	}
	
}