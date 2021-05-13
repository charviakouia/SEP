package dedede.model.logic.converters;

import dedede.model.logic.util.MediumPreviewPosition;
import jakarta.faces.convert.EnumConverter;

public class MediumPreviewPositionConverter extends EnumConverter {
	
	public MediumPreviewPositionConverter() {
		super(MediumPreviewPosition.class);
	}
}
