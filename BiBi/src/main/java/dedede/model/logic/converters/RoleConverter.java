package dedede.model.logic.converters;

import dedede.model.logic.util.Role;
import jakarta.faces.convert.EnumConverter;
/**
 * Checks if the input for a user role  is a legal.
 */

public class RoleConverter extends EnumConverter {

	public RoleConverter() {
		super(Role.class);
	}
}
