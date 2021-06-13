package de.dedede.model.logic.converters;

import de.dedede.model.data.dtos.MediumSearchCriterion;
import jakarta.faces.convert.EnumConverter;
import jakarta.faces.convert.FacesConverter;

/**
 * Converts a medium search criterion from and into a textual representation.
 */
@FacesConverter(forClass = MediumSearchCriterion.class)
public class MediumSearchCriterionConverter extends EnumConverter {

	public MediumSearchCriterionConverter() {
		super(MediumSearchCriterion.class);
	}
}

