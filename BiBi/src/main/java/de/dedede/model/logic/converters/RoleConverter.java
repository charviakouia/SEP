package de.dedede.model.logic.converters;

import de.dedede.model.logic.util.UserRole;
import jakarta.faces.convert.EnumConverter;

/**
 * Checks if the input for a user role is a legal.
 */

public class RoleConverter extends EnumConverter {

	public RoleConverter() {
		super(UserRole.class);
	}
}
