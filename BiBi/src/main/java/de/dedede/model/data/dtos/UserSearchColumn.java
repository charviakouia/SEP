package de.dedede.model.data.dtos;

/**
 * 
 * @author León Liehr
 */
public enum UserSearchColumn {
	EMAIL_ADDRESS, FIRST_NAME, LAST_NAME;

	@Override
	public String toString() {
		return switch (this) {
		case EMAIL_ADDRESS -> "userEmailAddress";
		case FIRST_NAME -> "userFirstName";
		case LAST_NAME -> "userLastName";
		};
	}
}
