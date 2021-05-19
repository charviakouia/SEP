package de.dedede.model.data.dtos;

import de.dedede.model.logic.util.AttributeType;
import de.dedede.model.logic.util.MediumPreviewPosition;
import de.dedede.model.logic.util.Multiplicity;

import java.awt.*;
import java.net.URL;
import java.util.LinkedList;

/**
 * This DTO (data transfer object) is responsible for aggregating and
 * encapsulating data about the attribute of the medium for transfer.
 * <p>
 * See the {@link de.dedede.model.persistence.daos.ApplicationDao} class to which this DTO is passed.
 *
 * @author Sergei Pravdin
 */
public class AttributeDto {

    private Integer id;

    private String name;

    private LinkedList<String> textValue;

    private LinkedList<Image> imageValue;

    private LinkedList<URL> urlValue;

    private AttributeType type;

    private Multiplicity multiplicity;

    private MediumPreviewPosition position;

    /**
     * Fetches the id of the attribute.
     *
     * @return An unique ID of the attribute.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets an ID to a attribute.
     * ID is a unique attribute's key used to identify the attribute from the database.
     *
     * @param id An ID to the necessary attribute.
     * @see de.dedede.model.persistence.daos.MediumDao
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Fetches the name of the attribute.
     *
     * @return A name of the attribute.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the attribute.
     *
     * @param name A name of the attribute.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Fetches the typ of the attribute.
     *
     * @return A typ of the attribute.
     * @see AttributeType
     */
    public AttributeType getType() {
        return type;
    }

    /**
     * Sets the typ of the attribute.
     *
     * @param type A typ of the attribute.
     * @see AttributeType
     */
    public void setType(AttributeType type) {
        this.type = type;
    }

    /**
     * Fetches the multiplicity status of the attribute.
     *
     * @return A multiplicity status of the attribute.
     * @see Multiplicity
     */
    public Multiplicity getMultiplicity() {
        return multiplicity;
    }

    /**
     * Sets the multiplicity status of the attribute.
     *
     * @param multiplicity A multiplicity status of the attribute.
     * @see Multiplicity
     */
    public void setMultiplicity(Multiplicity multiplicity) {
        this.multiplicity = multiplicity;
    }

    /**
     * Fetches a list of text values for the attribute.
     * The list contains more than one value if the attribute is multi-valued.
     *
     * @return A list of text values for the attribute.
     * @see Multiplicity
     */
    public LinkedList<String> getTextValue() {
        return textValue;
    }

    /**
     * Sets a list of text values for the attribute.
     * The list contains more than one value if the attribute is multi-valued.
     *
     * @param textValue A list of text values for the attribute.
     * @see Multiplicity
     */
    public void setTextValue(LinkedList<String> textValue) {
        this.textValue = textValue;
    }

    /**
     * Fetches a list of image values for the attribute.
     * The list contains more than one value if the attribute is multi-valued.
     *
     * @return A list of image values for the attribute.
     * @see Multiplicity
     */
    public LinkedList<Image> getImageValue() {
        return imageValue;
    }

    /**
     * Sets a list of image values for the attribute.
     * The list contains more than one value if the attribute is multi-valued.
     *
     * @param imageValue A list of image values for the attribute.
     * @see Multiplicity
     */
    public void setImageValue(LinkedList<Image> imageValue) {
        this.imageValue = imageValue;
    }

    /**
     * Fetches a list of URL values for the attribute.
     * The list contains more than one value if the attribute is multi-valued.
     *
     * @return A list of URL values for the attribute.
     * @see Multiplicity
     */
    public LinkedList<URL> getUrlValue() {
        return urlValue;
    }

    /**
     * Sets a list of URL values for the attribute.
     * The list contains more than one value if the attribute is multi-valued.
     *
     * @param urlValue A list of URL values for the attribute.
     * @see Multiplicity
     */
    public void setUrl(LinkedList<URL> urlValue) {
        this.urlValue = urlValue;
    }

    /**
     * Fetches the position of the attribute where it will be displayed on the page
     * of the medium to which it belongs.
     *
     * @return A position of the attribute.
     * @see MediumPreviewPosition
     */
    public MediumPreviewPosition getPosition() {
        return position;
    }

    /**
     * Sets the position of the attribute where it will be displayed on the page
     * of the medium to which it belongs.
     *
     * @param position A position of the attribute.
     * @see MediumPreviewPosition
     */
    public void setPosition(MediumPreviewPosition position) {
        this.position = position;
    }
}
