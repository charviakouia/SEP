package dedede.model.data.dtos;

import dedede.model.logic.util.AttributeType;
import dedede.model.logic.util.MediumPreviewPosition;
import dedede.model.logic.util.Multiplicity;

/**
 * A class for aggregate and encapsulate data about an medium's attribute for transfer.
 */
public class AttributeDto {

	private Integer id;
	
	private String name;
	
	private String value;
	
	private AttributeType type;
	
	private Multiplicity multiplicity;
	
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

	public Multiplicity getMultiplicity() {
		return multiplicity;
	}

	public void setMultiplicity(Multiplicity multiplicity) {
		this.multiplicity = multiplicity;
	}

	
}
