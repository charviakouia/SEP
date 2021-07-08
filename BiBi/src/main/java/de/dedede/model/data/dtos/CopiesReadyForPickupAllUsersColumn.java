package de.dedede.model.data.dtos;

/**
 * The columns of the paginated list found on the page listing all the copies ready for pickup of any user.
 * 
 * @author León Liehr
 */
public enum CopiesReadyForPickupAllUsersColumn {
	SIGNATURE, LOCATION, TITLE, USER, DEADLINE;

	@Override
	public String toString() {
		return switch (this) {
		case SIGNATURE -> "copy";
		case LOCATION -> "location";
		case TITLE -> "medium_attribute.title";
		case USER -> "user";
		case DEADLINE -> "end_of_pickup_period";
		};
	}
}
