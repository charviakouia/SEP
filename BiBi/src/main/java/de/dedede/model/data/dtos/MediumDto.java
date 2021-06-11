package de.dedede.model.data.dtos;

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

    private String title;

    private String author1;
    private String author2;
    private String author3;
    private String author4;
    private String author5;

    private String mediumType;

    private String edition;

    private String publisher;

    private String releaseYear;

    private String isbn;

    private String mediumLink;

    private String text;


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
    public void addAttribute(Integer key, AttributeDto value) {
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
    public void addCopy(Integer key, CopyDto value) {
        copies.put(key, value);
    }

    public Map<Integer, AttributeDto> getAttributes() {
        return attributes;
    }

    public Map<Integer, CopyDto> getCopies() {
        return copies;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor1() {
        return author1;
    }

    public void setAuthor1(String author1) {
        this.author1 = author1;
    }

    public String getAuthor2() {
        return author2;
    }

    public void setAuthor2(String author2) {
        this.author2 = author2;
    }

    public String getAuthor3() {
        return author3;
    }

    public void setAuthor3(String author3) {
        this.author3 = author3;
    }

    public String getAuthor4() {
        return author4;
    }

    public void setAuthor4(String author4) {
        this.author4 = author4;
    }

    public String getAuthor5() {
        return author5;
    }

    public void setAuthor5(String author5) {
        this.author5 = author5;
    }

    public String getMediumType() {
        return mediumType;
    }

    public void setMediumType(String mediumType) {
        this.mediumType = mediumType;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(String releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getMediumLink() {
        return mediumLink;
    }

    public void setMediumLink(String mediumLink) {
        this.mediumLink = mediumLink;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}