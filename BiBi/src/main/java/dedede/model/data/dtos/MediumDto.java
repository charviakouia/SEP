package dedede.model.data.dtos;

import java.time.Duration;
import java.util.Set;

/**
 * A class for aggregate and encapsulate data about a medium for transfer.
 * <p>
 * The class contains set CopyDTOs and AttributeDTOs.
 * See {@link CopyDto} and {@link AttributeDto} for more details about that.
 */
public class MediumDto {
	
	private Integer id;
	
	private Set<AttributeDto> attributes;

	private Set<CopyDto> copies;
	
	private String category;
	
	private Duration returnPeriod;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Set<AttributeDto> getAttributes() {
		return attributes;
	}

	public void setAttributes(Set<AttributeDto> attributes) {
		this.attributes = attributes;
	}
	

	public Set<CopyDto> getCopies() {
		return copies;
	}

	public void setCopies(Set<CopyDto> copies) {
		this.copies = copies;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Duration getReturnPeriod() {
		return returnPeriod;
	}

	public void setReturnPeriod(Duration returnPeriod) {
		this.returnPeriod = returnPeriod;
	}

}
