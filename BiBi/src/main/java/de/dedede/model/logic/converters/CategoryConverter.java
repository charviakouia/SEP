package de.dedede.model.logic.converters;

import de.dedede.model.data.dtos.CategoryDto;
import de.dedede.model.persistence.daos.CategoryDao;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.convert.FacesConverter;

@FacesConverter(value = "CategoryConverter")
public class CategoryConverter implements Converter<CategoryDto> {

	@Override
	public CategoryDto getAsObject(FacesContext arg0, UIComponent arg1, String arg2) throws ConverterException {
		try {
			CategoryDto result = new CategoryDto();
			result.setId(Integer.parseInt(arg2));
			return result;
		} catch (NumberFormatException e) {
			throw new ConverterException("Couldn't read the category's ID while converting", e);
		}
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, CategoryDto arg2) {
		return String.valueOf(arg2.getId());
	}

}
