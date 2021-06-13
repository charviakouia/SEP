package de.dedede.model.logic.converters;

import de.dedede.model.data.dtos.MediumSearchCriterion;
import de.dedede.model.persistence.util.Logger;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;

/**
 * Converts the search if it is for category or attribute.
 */
@FacesConverter(forClass = MediumSearchCriterion.class)
public class SearchCriterionConverter implements Converter<MediumSearchCriterion> {
	
	@Override
	public MediumSearchCriterion getAsObject(FacesContext ctx, UIComponent component, String input) {
		Logger.development("SearchCriterionConvert| input = " + input);
		
		final var result = MediumSearchCriterion.parse(input);
		
		Logger.development("SearchCriterionConvert| result = " + result);
		
		return result;
	}

	@Override
	public String getAsString(FacesContext ctx, UIComponent component, MediumSearchCriterion criterion) {
		final var r = criterion.toString();
		Logger.development("SearchCriterionConvert.getAsString| r=" + r);
		return r;
	}
}
