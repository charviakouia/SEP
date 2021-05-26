package de.dedede.model.logic.converters;

import de.dedede.model.data.dtos.UserRole;
import jakarta.faces.convert.EnumConverter;
import jakarta.faces.convert.FacesConverter;

/**
 * Checks if the input for a user role is a legal.
 */

@FacesConverter(forClass = UserRole.class)
public class RoleConverter extends EnumConverter {

	public RoleConverter() {
		super(UserRole.class);
	}
}
