package de.dedede.model.logic.converters;

import de.dedede.model.data.dtos.CopyStatus;
import jakarta.faces.convert.EnumConverter;
import jakarta.faces.convert.FacesConverter;

/**
 * Converts the availability status of an borrowed copy.
 */
@FacesConverter(forClass = CopyStatus.class)
public class AvailabilityStatusConverter extends EnumConverter {

	public AvailabilityStatusConverter() {
		super(CopyStatus.class);
	}

}
