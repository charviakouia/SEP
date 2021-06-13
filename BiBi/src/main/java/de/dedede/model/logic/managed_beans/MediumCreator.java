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

	private String categorySearchTerm;
	private List<CategoryDto> categories = new LinkedList<>();

	@PostConstruct
	public void init(){
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

}
