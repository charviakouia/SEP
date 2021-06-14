package de.dedede.model.data.dtos;

/**
 * Represents the permission roles users have in the system.
 */
public enum UserRole {
	REGISTERED, STAFF, ADMIN;

	public boolean isStaffOrHigher() {
		return compareTo(STAFF) >= 0;
	}
	
	@Override
	public String toString() {
		return switch(this) {
		case REGISTERED -> "user_role.registered";
		case STAFF -> "user_role.staff";
		case ADMIN -> "user_role.admin";
		};
	}
	
}
