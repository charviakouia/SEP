package de.dedede.model.data.dtos;

import jakarta.annotation.PostConstruct;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * This DTO (data transfer object) is responsible for aggregating and
 * encapsulating data about a medium for transfer.
 * <p>
 * See the {@link CopyDto}, {@link CategoryDto} and {@link AttributeDto} classes for the used DTOs as attributes.
 *
 * @author Sergei Pravdin
 */
public class MediumDto {

    private Integer id;

    private final Map<Integer, AttributeDto> attributes = new HashMap<Integer, AttributeDto>();

    private final Map<Integer, CopyDto> copies = new HashMap<Integer, CopyDto>();

    private CategoryDto category;

    private Duration returnPeriod;

    @PostConstruct
    private void init() {
        category = new CategoryDto();
    }

    /**
     * Fetches the id of the medium.
     *
     * @return An unique ID of the medium.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets an ID to a medium.
     * ID is a unique medium's key used to identify the medium from the database.
     *
     * @param id An ID to the necessary medium.
     * @see de.dedede.model.persistence.daos.MediumDao
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Fetches the category to which belongs to the medium.
     * A medium can only belong to one category.
     *
     * @return A medium's category.
     * @see CategoryDto
     */
    public CategoryDto getCategory() {
        return category;
    }

    public void setCategory(CategoryDto category) {
        this.category = category;
    }

    /**
     * Fetches a return period for a medium that is available to a user.
     * A user can return a medium earlier than the return period.
     *
     * @return A medium's return period.
     * @see Duration
     */
    public Duration getReturnPeriod() {
        return returnPeriod;
    }

    /**
     * Sets a return period for a medium that is available to a user.
     * A return period cannot be shorter than an actual lending.
     *
     * @param returnPeriod A medium's return period.
     * @see Duration
     */
    public void setReturnPeriod(Duration returnPeriod) {
        this.returnPeriod = returnPeriod;
    }

    /**
     * Fetches an attributeDto with the data of the attribute that belongs to this medium.
     *
     * @param key Unique attribute ID, which corresponds to the ID in the database.
     * @return An attributeDto with the data of the attribute.
     * @see AttributeDto
     */
    public AttributeDto getAttribute(Integer key) {
        return attributes.get(key);
    }

    /**
     * Sets an attributeDto with the data of the attribute that belongs to this medium.
     *
     * @param key Unique attribute ID, which corresponds to the ID in the database.
     * @param value An attributeDto with the data of the attribute which should be assigned to this medium.
     * @see AttributeDto
     */
    public void setAttribute(Integer key, AttributeDto value) {
        attributes.put(key, value);
    }

    /**
     * Fetches a CopyDto with the data of the copy that belongs to this medium.
     *
     * @param key Unique copy ID, which corresponds to the ID in the database.
     * @return A CopyDto with the data of the copy.
     * @see CopyDto
     */
    public CopyDto getCopy(Integer key) {
        return copies.get(key);
    }

    /**
     * Sets a CopyDto with the data of the copy that belongs to this medium.
     *
     * @param key Unique copy ID, which corresponds to the ID in the database.
     * @param value A CopyDto with the data of the copy which should be assigned to this medium.
     * @see CopyDto
     */
    public void setCopy(Integer key, CopyDto value) {
        copies.put(key, value);
    }

    public Map<Integer, AttributeDto> getAttributes() {
        return attributes;
    }

    public Map<Integer, CopyDto> getCopies() {
        return copies;
    }
}