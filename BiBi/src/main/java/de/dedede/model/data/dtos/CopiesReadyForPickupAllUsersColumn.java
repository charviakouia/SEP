package de.dedede.model.data.dtos;

/**
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
