package de.dedede.model.data.dtos;

/**
 *
 */
public enum AttributeMultiplicity {
	SINGLE_VALUED, MULTI_VALUED;
	
	@Override
	public String toString() {
		return switch(this) {
		case SINGLE_VALUED -> "attribute_multiplicity.single_valued";
		case MULTI_VALUED -> "attribute_multiplicity.multi_valued";
		};
	}
}
