package de.dedede.model.data.dtos;

/**
 * A class for aggregate and encapsulate data about a medium's caategory for
 * transfer.
 */
public class CategoryDto {

	private int id;

	private String name;

	private String description;

	private CategoryDto parent;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public CategoryDto getParent() {
		return parent;
	}

	public void setParent(CategoryDto parent) {
		this.parent = parent;
	}

}
