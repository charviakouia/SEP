package de.dedede.model.logic.converters;

import de.dedede.model.data.dtos.UserRole;
import de.dedede.model.logic.util.MediumType;
import jakarta.faces.convert.EnumConverter;
import jakarta.faces.convert.FacesConverter;

@FacesConverter(forClass = MediumType.class)
public class MediumTypeConverter extends EnumConverter {
    public MediumTypeConverter() {
        super(MediumType.class);
    }
}
