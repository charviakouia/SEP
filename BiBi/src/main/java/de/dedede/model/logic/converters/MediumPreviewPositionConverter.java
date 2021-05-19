package de.dedede.model.logic.converters;

import de.dedede.model.logic.util.MediumPreviewPosition;
import jakarta.faces.convert.EnumConverter;

/**
 * Converts the preview position of an medium.
 */
public class MediumPreviewPositionConverter extends EnumConverter {

	public MediumPreviewPositionConverter() {
		super(MediumPreviewPosition.class);
	}
}