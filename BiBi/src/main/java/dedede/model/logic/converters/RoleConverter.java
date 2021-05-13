package dedede.model.logic.converters;

import dedede.model.logic.util.Role;
import jakarta.faces.convert.EnumConverter;

public class RoleConverter extends EnumConverter {

	public RoleConverter() {
		super(Role.class);
	}
}
