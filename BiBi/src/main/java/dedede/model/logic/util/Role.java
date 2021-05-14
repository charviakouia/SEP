package dedede.model.logic.util;

/**
 * Represents the permission roles users have in the system.
 */
public enum Role {
	ADMIN, LIBRARY_STAFF, NORMAL_USER;

	public boolean hasPermissions(Role that) {
		return true;
	}

}
