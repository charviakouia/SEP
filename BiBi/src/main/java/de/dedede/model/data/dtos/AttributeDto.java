package de.dedede.model.data.dtos;

import de.dedede.model.logic.util.AttributeModifiability;
import de.dedede.model.logic.util.AttributeType;
import de.dedede.model.logic.util.MediumPreviewPosition;
import de.dedede.model.logic.util.AttributeMultiplicity;

/**
 * A class for aggregate and encapsulate data about an medium's attribute for
 * transfer.
 */
public class AttributeDto {

	private Integer id;

	private String name;

	private String value;

	private AttributeModifiability attributeModifiability;

	private AttributeType type;

	private AttributeMultiplicity attributeMultiplicity;

	private MediumPreviewPosition position;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public AttributeType getType() {
		return type;
	}

	public void setType(AttributeType type) {
		this.type = type;
	}

	public AttributeMultiplicity getMultiplicity() {
		return attributeMultiplicity;
	}

	public void setMultiplicity(AttributeMultiplicity attributeMultiplicity) {
		this.attributeMultiplicity = attributeMultiplicity;
	}
	public AttributeModifiability getAttributeModifiability() {
		return attributeModifiability;
	}

	public void setAttributeModifiability(AttributeModifiability attributeModifiability) {
		this.attributeModifiability = attributeModifiability;
	}

}
