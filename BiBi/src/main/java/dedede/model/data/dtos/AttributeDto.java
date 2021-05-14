package dedede.model.data.dtos;

/**
 * A class for aggregate and encapsulate data about an medium's attribute for transfer.
 */
public class AttributeDto {

	private Integer id;
	
	private String name;
	
	private String value;
	
	private String attributeType;

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

	public String getAttributeType() {
		return attributeType;
	}

	public void setAttributeType(String attributeType) {
		this.attributeType = attributeType;
	}

}
