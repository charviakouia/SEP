package de.dedede.model.logic.util;

import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.persistence.util.Logger;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Hashes the Password of the {@link UserDto} with SHA-256, before it is stored
 * in the database.
 */
public final class PasswordHashingModule {
    private PasswordHashingModule() {
        throw new IllegalStateException();
    }

    /**
     * Hash a password with the SHA-256 hash function.
     *
     * @param password The String which should be hashed.
     * @return password in a hash as String.
     */
    public static String hashPassword(String password, String salt) {
        String hashedPassword = "";
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] encodedHash = messageDigest.digest(password.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder(2 * encodedHash.length);
            for (int i = 0; i < encodedHash.length; i++) {
                sb.append(Integer.toString((encodedHash[i] & 0xff) + 0x100, 16).substring(1));
            }
            hashedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            //Logger.development("The SHA3-256 does not exist");
            e.printStackTrace();
        }

        return hashedPassword;
    }
}