package de.dedede.model.data.dtos;

import com.sun.mail.iap.ByteArray;
import de.dedede.model.logic.util.AttributeType;

import java.awt.*;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This DTO (data transfer object) is responsible for aggregating and
 * encapsulating data about a medium for transfer.
 * <p>
 * See the {@link CopyDto} and {@link AttributeDto} classes for the used DTOs as attributes.
 *
 * @author Sergei Pravdin
 */
public class MediumDto {

    private Integer id;

    private final Map<Integer, String> textAttributes = new HashMap<Integer, String>();

    private final Map<Integer, Image> imagesAttributes = new HashMap<Integer, java.awt.Image>();

    private final Map<Integer, java.net.URL> linkAttributes = new HashMap<Integer, java.net.URL>();

    private Set<AttributeDto> attributes;

    private Set<CopyDto> copies;

    private CategoryDto category;

    private Duration returnPeriod;

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
     * ID is a unique medium key used to identify the medium from the database.
     *
     * @param id An ID to the necessary medium.
     * @see de.dedede.model.persistence.daos.MediumDao
     */
    public void setId(Integer id) {
        this.id = id;
    }

    public Set<AttributeDto> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<AttributeDto> attributes) {
        this.attributes = attributes;
    }

    /**
     * Fetches a set of DTO containers with the data of the copies that belong to this medium.
     * The order of the data and concurrent behaviour is unspecified.
     *
     * @return A set of DTO containers with the data of the copies.
     * @see CopyDto
     */
    public Set<CopyDto> getCopies() {
        return copies;
    }

    /**
     * Sets a set of DTO containers with the data of the copies that belong to this medium.
     *
     * @param copies A set of DTO containers with the data of the copies.
     * @see CopyDto
     */
    public void setCopies(Set<CopyDto> copies) {
        this.copies = copies;
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

    public String getTextAttributes(int key) {
        return textAttributes.get(key);
    }

    public void setTextAttributes(int key, String value) {
        textAttributes.put(key, value);
    }

    public Image getImagesAttributes(int key) {
        return imagesAttributes.get(key);
    }

    public void setImageAttributes(int key, Image value) {
        imagesAttributes.put(key, value);
    }

    public URL getLinkAttributes(int key) {
        return linkAttributes.get(key);
    }

    public void setLinkAttributes(int key, URL value) {
        linkAttributes.put(key, value);
    }
}
