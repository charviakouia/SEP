package de.dedede.model.logic.converters;

import de.dedede.model.logic.util.MediumType;
import jakarta.faces.convert.EnumConverter;
import jakarta.faces.convert.FacesConverter;

/**
 * Converts generic medium types, as represented by a category DTO {@link MediumType}, into a textual
 * representation and vice-versa.
 *
 * @author Ivan Charviakou
 */
@FacesConverter(forClass = MediumType.class)
public class MediumTypeConverter extends EnumConverter {
    public MediumTypeConverter() {
        super(MediumType.class);
    }
}
