package de.dedede.model.logic.converters;

import de.dedede.model.data.dtos.SearchOperator;
import jakarta.faces.convert.EnumConverter;
import jakarta.faces.convert.FacesConverter;

/**
 * Checks if the input for this search field is a legal.
 */
@FacesConverter(forClass = SearchOperator.class)
public class SearchOperatorConverter extends EnumConverter {

	public SearchOperatorConverter() {
		super(SearchOperator.class);
	}
}
