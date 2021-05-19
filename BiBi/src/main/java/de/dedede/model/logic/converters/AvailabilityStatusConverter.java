package de.dedede.model.logic.converters;

import de.dedede.model.logic.util.AvailabilityStatus;
import jakarta.faces.convert.EnumConverter;

/**
 * Converts the availability status of an borrowed copy.
 */
public class AvailabilityStatusConverter extends EnumConverter {

	public AvailabilityStatusConverter() {
		super(AvailabilityStatus.class);
	}

}
