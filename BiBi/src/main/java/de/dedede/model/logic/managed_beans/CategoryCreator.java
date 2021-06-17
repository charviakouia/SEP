package de.dedede.model.logic.managed_beans;


import de.dedede.model.data.dtos.CategoryDto;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 *Backing bean for creating an category.
 */
@Named
@RequestScoped
public class CategoryCreator {

    private String defaultParentCategoryName = "SampleParentCategory";

    private CategoryDto category;

    private String parentCategoryName;

    @Inject
    FacesContext context;

    @Inject
    UserSession session;

    @PostConstruct
	public void init() {
        category = new CategoryDto();
        CategoryDto parentCategory = new CategoryDto();
        category.setParent(parentCategory);
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
        if (parentCategoryName.isEmpty()) {
            parentCategoryName = defaultParentCategoryName;
            category.getParent().setName(parentCategoryName);
        }
    }

    public String getParentCategoryName() {
        return parentCategoryName;
    }

    public void setParentCategoryName(String parentCategoryName) {
        this.parentCategoryName = parentCategoryName;
    }
}
