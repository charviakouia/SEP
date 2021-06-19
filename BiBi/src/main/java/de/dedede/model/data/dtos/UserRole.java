package de.dedede.model.data.dtos;

/**
 * Represents the permission roles users have in the system.
 * 
 * @author León Liehr
 */
public enum UserRole {
	/**
	 * The permission role of a normal logged-in user. It is the lowest possible
	 * role.
	 */
	REGISTERED,
	/**
	 * The permission role of library staff.
	 */
	STAFF,
	/**
	 * The permission role of administrators. It is the highest possible role.
	 */
	ADMIN;

	/**
	 * Indicates whether a user has the permission of library staff or more than
	 * that. Practically speaking, it shows if a user is library staff or an
	 * administrator.
	 * 
	 * @return
	 */
	public boolean isStaffOrHigher() {
		return compareTo(STAFF) >= 0;
	}

	/**
	 * Returns the textual representation of a user role which in this case is a key
	 * into the i18n resource bundle.
	 */
	@Override
	public String toString() {
		return switch (this) {
		case REGISTERED -> "user_role.registered";
		case STAFF -> "user_role.staff";
		case ADMIN -> "user_role.admin";
		};
	}

}
