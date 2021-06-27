package de.dedede.model.logic.converters;

import de.dedede.model.data.dtos.CategoryDto;
import de.dedede.model.logic.util.MessagingUtility;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.convert.FacesConverter;

/**
 * Converts categories, as represented by a category DTO {@link CategoryDto}, into a textual representation and
 * vice-versa. In particular, the ID is used to produce the text and to look up the category entity in the datastore.
 *
 * @author Ivan Charviakou
 */
@FacesConverter(value = "CategoryConverter")
public class CategoryConverter implements Converter<CategoryDto> {

	@Override
	public CategoryDto getAsObject(FacesContext context, UIComponent arg1, String arg2) throws ConverterException {
		try {
			CategoryDto result = new CategoryDto();
			result.setId(Integer.parseInt(arg2));
			return result;
		} catch (NumberFormatException e) {
			String msg = MessagingUtility.getMessage(context, "categoryConverter.error");
			throw new ConverterException(msg, e);
		}
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, CategoryDto arg2) {
		return String.valueOf(arg2.getId());
	}

}
