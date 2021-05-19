package de.dedede.model.data.dtos;

/**
 * This DTO (data transfer object) is responsible for aggregating and
 * encapsulating data about the category of the medium for transfer.
 * <p>
 * See the {@link de.dedede.model.persistence.daos.CategoryDao} class to which this DTO is passed.
 *
 * @author Sergei Pravdin
 */
public class CategoryDto {

	private int id;

	private String name;

	private String description;

	private CategoryDto parent;

	/**
	 * Fetches the id of the category.
	 *
	 * @return An unique ID of the category.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the id of the category.
	 *
	 * @param id An unique ID of the category.
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Fetches the name of the category.
	 *
	 * @return A name of the category.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the category.
	 *
	 * @param name A name of the category.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Fetches the description of the category.
	 *
	 * @return A description of the category.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description of the category.
	 *
	 * @param description A description of the category.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Fetches the parent category of this category.
	 * Categories can be enclosed in each other without limitation.
	 * A category can have only one direct parent category and more than one direct child category.
	 *
	 * @return A parent category of this category.
	 */
	public CategoryDto getParent() {
		return parent;
	}

	/**
	 * Fetches the parent category of this category.
	 * Categories can be enclosed in each other without limitation.
	 * A category can have only one direct parent category and more than one direct child category.
	 *
	 * @param parent A parent category of this category.
	 */
	public void setParent(CategoryDto parent) {
		this.parent = parent;
	}

}
