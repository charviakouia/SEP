package de.dedede.model.logic.managed_beans;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import de.dedede.model.data.dtos.CategoryDto;
import de.dedede.model.data.dtos.CategorySearchDto;
import de.dedede.model.data.dtos.MediumDto;
import jakarta.annotation.PostConstruct;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Backing bean for the category browser facelet. This page offers users to
 * browse through all available categories mediums are part of in a structured
 * manner. It renders it possible for the consumer to discover new mediums.
 *
 */
@Named
@ViewScoped
public class CategoryBrowser extends PaginatedList implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;
	
	@Inject
	private ExternalContext ectx;
	
	@Inject
	private UserSession session;

	private CategorySearchDto categorySearch;

	private CategoryDto currentCategory;
	
	private List<MediumDto> mediums;


	@PostConstruct
	public void init() {

	}

	public CategorySearchDto getCategorySearch() {
		return categorySearch;
	}

	public void setCategorySearch(CategorySearchDto categorySearch) {
		this.categorySearch = categorySearch;
	}

	public CategoryDto getCurrentCategory() {
		return currentCategory;
	}

	public void setCurrentCategory(CategoryDto currentCategory) {
		this.currentCategory = currentCategory;
	}
	
	public boolean writableCategoryName() {
		if (session.getUser() == null) {
			return false;
		}
		
		return session.getUser().getRole().isStaffOrHigher();
	}
	
	public boolean writableCategoryDescription() {
		if (session.getUser() == null) {
			return false;
		}
		
		return session.getUser().getRole().isStaffOrHigher();
	}

	public void deleteCategory() {

	}

	public void searchCategories() {

	}
	
	public String createCategory() {
		ectx.getFlash().put("parent-category", currentCategory.getId());
		
		return "category-creation?faces-redirect=true";
	}

	@Override
	public List<MediumDto> getItems() {
		return mediums;
	}

	@Override
	public void refresh() {
		searchCategories();
	}
}
