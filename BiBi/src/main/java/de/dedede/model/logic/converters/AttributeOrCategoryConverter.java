package de.dedede.model.logic.converters;

import de.dedede.model.logic.util.AttributeOrCategory;
import jakarta.faces.convert.EnumConverter;
import jakarta.faces.convert.FacesConverter;

/**
 * Converts the search
 */
@FacesConverter(forClass = AttributeOrCategory.class)
public class AttributeOrCategoryConverter extends EnumConverter {

	public AttributeOrCategoryConverter() {
		super(AttributeOrCategory.class);
	}
}
