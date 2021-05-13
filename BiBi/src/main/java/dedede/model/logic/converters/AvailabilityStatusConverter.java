package dedede.model.logic.converters;

import dedede.model.logic.util.AvailabilityStatus;
import jakarta.faces.convert.EnumConverter;

public class AvailabilityStatusConverter extends EnumConverter {

	public AvailabilityStatusConverter() {
		super(AvailabilityStatus.class);
	}
	
}
