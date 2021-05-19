package de.dedede.model.logic.converters;

import de.dedede.model.logic.util.SearchOperator;
import jakarta.faces.convert.EnumConverter;

/**
 * Checks if the input for this search field is a legal.
 */
public class SearchOperatorConverter extends EnumConverter {

	public SearchOperatorConverter() {
		super(SearchOperator.class);
	}
}