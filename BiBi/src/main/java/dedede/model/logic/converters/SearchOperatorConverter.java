package dedede.model.logic.converters;

import dedede.model.logic.util.SearchOperator;
import jakarta.faces.convert.EnumConverter;

public class SearchOperatorConverter extends EnumConverter {
	
	public SearchOperatorConverter() {
		super(SearchOperator.class);
	}
}
