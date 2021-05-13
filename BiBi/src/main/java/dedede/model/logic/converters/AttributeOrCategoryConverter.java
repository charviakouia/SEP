package dedede.model.logic.converters;

import dedede.model.logic.util.AttributeOrCategory;
import jakarta.faces.convert.EnumConverter;

public class AttributeOrCategoryConverter extends EnumConverter {
	
	public AttributeOrCategoryConverter() {
		super(AttributeOrCategory.class);
	}
}
