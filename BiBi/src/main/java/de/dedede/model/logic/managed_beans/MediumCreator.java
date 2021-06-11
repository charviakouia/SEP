package de.dedede.model.logic.managed_beans;

import java.awt.*;
import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.net.URL;
import java.util.*;
import java.util.List;

import de.dedede.model.data.dtos.*;
import de.dedede.model.persistence.daos.CategoryDao;
import de.dedede.model.persistence.daos.MediumDao;
import de.dedede.model.persistence.exceptions.EntityInstanceNotUniqueException;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.servlet.http.Part;

import javax.imageio.ImageIO;

/**
 * Backing bean for the medium creator facelet.
 */
@Named
@ViewScoped
public class MediumCreator implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;
	private static final int NUM_DISPLAYED_CATEGORY_ENTRIES = 10;

	private MediumDto medium;
	private CopyDto copy;

	private Map<AttributeDto, String> stringAttributeEntries = new HashMap<>();
	private Map<AttributeDto, List<URL>> urlAttributeEntries = new HashMap<>();
	private Map<AttributeDto, Part> imageAttributeEntries = new HashMap<>();

	private String categorySearchTerm;
	private List<CategoryDto> categories = new LinkedList<>();

	@PostConstruct
	public void initializeBean(){
		List<AttributeDto> dbAttributes = MediumDao.readGlobalAttributes();
		for (AttributeDto attribute : dbAttributes){
			switch (attribute.getType()) {
				case LINK -> urlAttributeEntries.put(attribute, null);
				case IMAGE -> imageAttributeEntries.put(attribute, null);
				case TEXT -> stringAttributeEntries.put(attribute, null);
			}
		}
	}

	private void processStringAttributes(){
		for (Map.Entry<AttributeDto, String> entry : stringAttributeEntries.entrySet()){
			AttributeDto attributeDto = entry.getKey();
			String inputValue = entry.getValue();
			attributeDto.setTextValue(List.of(inputValue.trim().split("\\s*,\\s*")));
			medium.addAttribute(attributeDto.getId(), attributeDto);
		}
	}

	private void processUrlAttributes(){
		for (Map.Entry<AttributeDto, List<URL>> entry : urlAttributeEntries.entrySet()){
			AttributeDto attributeDto = entry.getKey();
			List<URL> inputValue = entry.getValue();
			attributeDto.setUrl(inputValue);
			medium.addAttribute(attributeDto.getId(), attributeDto);
		}
	}

	public MediumDto getMedium() {
		return medium;
	}

	public void setMedium(MediumDto medium) {
		this.medium = medium;
	}

	public CopyDto getCopy() {
		return copy;
	}

	public void setCopy(CopyDto copy) {
		this.copy = copy;
	}

	public Set<Map.Entry<AttributeDto, String>> getStringAttributeEntries(){
		return stringAttributeEntries.entrySet();
	}

	public Set<Map.Entry<AttributeDto, List<URL>>> getUrlAttributeEntries(){
		return urlAttributeEntries.entrySet();
	}

	public Set<Map.Entry<AttributeDto, Part>> getImageAttributeEntries(){
		return imageAttributeEntries.entrySet();
	}

	public List<CategoryDto> getSearchedCategories(){
		return categories;
	}

	public String getCategorySearchTerm() {
		return categorySearchTerm;
	}

	public void setCategorySearchTerm(String categorySearchTerm) {
		this.categorySearchTerm = categorySearchTerm;
	}

	// Navigation methods:

	/**
	 * Creates this medium and its first copy.
	 */
	public String save() throws IOException, EntityInstanceNotUniqueException {
		processStringAttributes();
		processUrlAttributes();
		MediumDao.createMedium(medium);
		return null;
	}

	public String searchForCategory(){
		PaginationDto paginationDto = new PaginationDto();
		paginationDto.setPageNumber(1);
		paginationDto.setTotalAmountOfRows(NUM_DISPLAYED_CATEGORY_ENTRIES);
		CategorySearchDto categorySearchDto = new CategorySearchDto();
		categorySearchDto.setSearchTerm(categorySearchTerm);
		categories = CategoryDao.readCategoriesByName(categorySearchDto, paginationDto);
		return null;
	}

	// Helper methods:

	public void upload(Map.Entry<AttributeDto, Part> entry) throws IOException {
		AttributeDto attributeDto = entry.getKey();
		Part inputValue = entry.getValue();
		Image img = ImageIO.read(inputValue.getInputStream());
		attributeDto.setImageValue(List.of(img));
	}

}
