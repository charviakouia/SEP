package de.dedede.model.logic.managed_beans;


import de.dedede.model.data.dtos.CategoryDto;
import de.dedede.model.data.dtos.UserRole;
import de.dedede.model.persistence.daos.CategoryDao;
import de.dedede.model.persistence.daos.UserDao;
import de.dedede.model.persistence.exceptions.CategoryDoesNotExistException;
import de.dedede.model.persistence.exceptions.ParentCategoryDoesNotExistException;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 *Backing bean for creating an category.
 */
@Named
@RequestScoped
public class CategoryCreator {

    private CategoryDto category;

    private CategoryDto parentCategory;

    @Inject
    FacesContext context;

    @Inject
    UserSession userSession;

    @PostConstruct
	public void init() {
        category = new CategoryDto();
        parentCategory = new CategoryDto();
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
        ResourceBundle messages =
                context.getApplication().evaluateExpressionGet(context, "#{msg}", ResourceBundle.class);
        try {
            CategoryDao.createCategory(category);
        } catch (ParentCategoryDoesNotExistException e) {
            context.addMessage(null, new FacesMessage(messages.getString("categoryCreator.notParentMatch")));
        }

    }

    public void onload() throws IOException  {
        ResourceBundle messages =
                context.getApplication().evaluateExpressionGet(context, "#{msg}", ResourceBundle.class);
        if (parentCategory.getId() != 0) {
            try {
                if (userSession.getUser() != null) {
                    if (userSession.getUser().getUserRole().equals(UserRole.ADMIN)
                            || userSession.getUser().getUserRole().equals(UserRole.STAFF)) {
                        parentCategory = CategoryDao.readCategory(parentCategory);
                    }
                } else {
                    context.addMessage(null, new FacesMessage(messages.getString("categoryCreator.notLogin")));
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().getExternalContext().redirect("/BiBi/view/public/login.xhtml?faces-redirect=true");
                }
            } catch (CategoryDoesNotExistException e) {
                context.addMessage(null, new FacesMessage(messages.getString("categoryCreator.invalidID")));
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().getExternalContext().redirect("/BiBi/view/staff/category-creator.xhtml?faces-redirect=true");
            }
        }

    }
}
