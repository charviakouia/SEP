package de.dedede.model.data.dtos;

/**
 * Represents the permission roles users have in the system.
 */
public enum UserRole {
	REGISTERED, STAFF, ADMIN;

	public boolean isStaffOrHigher() {
		return compareTo(STAFF) >= 0;
	}
}
