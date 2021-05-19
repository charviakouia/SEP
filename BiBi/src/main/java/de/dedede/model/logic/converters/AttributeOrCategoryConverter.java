package de.dedede.model.logic.converters;

import de.dedede.model.logic.util.AttributeOrCategory;
import jakarta.faces.convert.EnumConverter;

/**
 * Converts the search
 */
public class AttributeOrCategoryConverter extends EnumConverter {

	public AttributeOrCategoryConverter() {
		super(AttributeOrCategory.class);
	}
}
