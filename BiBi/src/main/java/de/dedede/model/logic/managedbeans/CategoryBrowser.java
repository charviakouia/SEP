package de.dedede.model.logic.managedbeans;

import de.dedede.model.data.dtos.CategoryDto;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;

/**
 * Backing bean for the category browser facelet. This page offers users to
 * browse through all available categories mediums are part of in a structured
 * manner. It renders it possible for the consumer to discover new mediums.
 *
 */
@Named
@RequestScoped
public class CategoryBrowser extends PaginatedList implements Serializable {

	@Serial
	private static  final long serialVersionUID = 1L;


	private CategoryDto category;

	private String searchTerm;

	public String getSearchTerm() {
		return searchTerm;
	}

	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}


	@PostConstruct
	public void init(){


	}



	public void deleteCategory() {

	}


	public void searchCategory() {

	}

	public CategoryDto getCategory() {
		return category;
	}

	public void setCategory(CategoryDto category) {
		this.category = category;
	}
}
