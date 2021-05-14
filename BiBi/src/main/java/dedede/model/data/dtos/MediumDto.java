package dedede.model.data.dtos;

import java.util.Set;

/**
 * A class for aggregate and encapsulate data about a medium for transfer.
 * <p>
 * The class contains set CopyDTOs and AttributeDTOs.
 * See {@link CopyDto} and {@link AttributeDto} for more details about that.
 */
public class MediumDto {
	
	private Integer id;
	
	private Set<AttributeDto> attribute;

	private Set<CopyDto> attribute;
	
	private String category;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Set<AttributeDto> getAttribute() {
		return attribute;
	}

	public void setAttribute(Set<AttributeDto> attribute) {
		this.attribute = attribute;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	

}
