package de.dedede.model.logic.managed_beans;


import de.dedede.model.data.dtos.CategoryDto;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

/**
 *Backing bean for creating an category.
 */
@Named
@RequestScoped
public class CategoryCreator {

    private CategoryDto category;

    @PostConstruct
	public void init() {

	}

    /**
     * fetching this category.
     * @return the category.
     */
    public CategoryDto getCategory() {
        return category;
    }

    public void setCategory(CategoryDto category) {
        this.category = category;
    }
    
    /**
     * creating this category.
     */
    public void createCategory(){

    }
}
