package dedede.model.logic.util;


/**
 * Hashes the Password of the {@link dedede.model.data.dtos.UserDto} with SHA512, before it is stored in the
 * database.
 */
public final class PasswordHashingModule {
	private PasswordHashingModule() {
		throw new IllegalStateException();
	}

	/**
	 * Hash a password with the SHA256 hash function.
	 *
	 * @param password The String which should be hashed.
	 * @return password in a hash as String.
	 */
	public static String hashPassword(String password) {
		return null;

	}
}