package dedede.model.logic.util;
/**
 * Represents the permission roles users have in the system.
 */
public enum Role {
	ADMIN, LIBRARYSTAFF, NORMALUSER;

	public boolean hasPermissions(Role this) {return true;}

}
