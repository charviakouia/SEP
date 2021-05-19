package de.dedede.model.logic.managedbeans;


import de.dedede.model.data.dtos.CategoryDto;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

@Named
@RequestScoped
public class CategoryCreator {

    private CategoryDto category;

    public void createCategory(){

    }

    public CategoryDto getCategory() {
        return category;
    }

    public void setCategory(CategoryDto category) {
        this.category = category;
    }
}
