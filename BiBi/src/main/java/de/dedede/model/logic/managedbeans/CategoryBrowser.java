package de.dedede.model.logic.managedbeans;

import de.dedede.model.data.dtos.CategoryDto;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import java.io.Serializable;

/**
 * Backing bean for the category browser facelet. This page offers users to
 * browse through all available categories mediums are part of in a structured
 * manner. It renders it possible for the consumer to discover new mediums.
 *
 */
@Named
@ViewScoped
public class CategoryBrowser extends PaginatedList implements Serializable {

	private String searchTerm;

	private CategoryDto category;

	public void createCategory() {

	}

	public void deleteCategory() {

	}

	public void editCategory() {

	}

	public void searchCategory() {

	}

}
