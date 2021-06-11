package de.dedede.model.logic.converters;

import de.dedede.model.data.dtos.SearchCriterion;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;

/**
 * Converts the search if it is for category or attribute.
 */
@FacesConverter(forClass = SearchCriterion.class)
public class SearchCriterionConverter implements Converter<SearchCriterion> {
	
	@Override
	public SearchCriterion getAsObject(FacesContext ctx, UIComponent component, String input) {
		// @Task use resource bundle here!
		if (input.equals("KATEGORIE")) {
			return new SearchCriterion.Category();
		} else {
			// @Task more validation
			return new SearchCriterion.Attribute(input);
		}
	}

	@Override
	public String getAsString(FacesContext ctx, UIComponent component, SearchCriterion criterion) {
		if (criterion instanceof SearchCriterion.Attribute) {
			final var attribute = (SearchCriterion.Attribute) criterion;
			
			return attribute.getValue();
		} else {
			return "KATEGORIE";
		}
	}
}
