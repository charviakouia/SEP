package de.dedede.model.logic.converters;

import de.dedede.model.data.dtos.MediumPreviewPosition;
import jakarta.faces.convert.EnumConverter;
import jakarta.faces.convert.FacesConverter;

/**
 * Converts the preview position of an medium.
 */
@FacesConverter(forClass = MediumPreviewPosition.class)
public class MediumPreviewPositionConverter extends EnumConverter {

	public MediumPreviewPositionConverter() {
		super(MediumPreviewPosition.class);
	}
}
