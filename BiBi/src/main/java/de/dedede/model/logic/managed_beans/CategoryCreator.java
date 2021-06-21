package de.dedede.model.logic.managed_beans;


import de.dedede.model.data.dtos.CategoryDto;
import de.dedede.model.data.dtos.UserRole;
import de.dedede.model.persistence.daos.CategoryDao;
import de.dedede.model.persistence.daos.UserDao;
import de.dedede.model.persistence.exceptions.CategoryDoesNotExistException;
import de.dedede.model.persistence.exceptions.EntityInstanceNotUniqueException;
import de.dedede.model.persistence.exceptions.ParentCategoryDoesNotExistException;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.UUID;

/**
 * Backing bean for creating an category.
 */
@Named
@RequestScoped
public class CategoryCreator {

    private CategoryDto category;

    private static final String defaultParentCategory = "SampleParentCategory";

    @Inject
    FacesContext context;

    @Inject
    UserSession userSession;

    @PostConstruct
    public void init() {
        category = new CategoryDto();
        CategoryDto parentCategory = new CategoryDto();
        category.setParent(parentCategory);
    }

    /**
     * Fetches a DTO container with data about the category being created.
     *
     * @return a DTO container with data about the category being created.
     */
    public CategoryDto getCategory() {
        return category;
    }


    /**
     * @param category Sets a DTO container with data about the category being created.
     */
    public void setCategory(CategoryDto category) {
        this.category = category;
    }

    /**
     * Creates a new category with the attributes specified in the facelet.
     * It is not possible to create a category with a caption that already exists.
     * The parent category must be correct, otherwise the user will receive an error message.
     *
     * @author Sergei Pravdin
     */
    public void createCategory() throws IOException {
        ResourceBundle messages =
                context.getApplication().evaluateExpressionGet(context, "#{msg}", ResourceBundle.class);
        try {
            category.setId(generateId());
            CategoryDao.createCategory(category);
            context.addMessage(null, new FacesMessage(messages.getString("categoryCreator.success")));
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().getExternalContext().redirect("/BiBi/view/public/category-browser.xhtml?faces-redirect=true");
        } catch (ParentCategoryDoesNotExistException e) {
            context.addMessage(null, new FacesMessage(messages.getString("categoryCreator.notParentMatch")));
        } catch (EntityInstanceNotUniqueException exception) {
            context.addMessage(null, new FacesMessage(messages.getString("categoryCreator.notUnique")));
        }
    }

    /**
     * Loads the category creation page. The status of the user who is trying to access
     * the page must be "STAFF" or "administrator", otherwise the user will receive an error message
     * and will be redirected to the home page. If the page receives a ViewParam, then the parent category
     * will be found and the name of the parent category will be displayed on the form. If there is no viewParam,
     * then the default parent category will be specified.
     *
     * @throws IOException, if the redirect is not possible.
     *
     * @author Sergei Pravdin
     */
    public void onload() throws IOException {
        ResourceBundle messages =
                context.getApplication().evaluateExpressionGet(context, "#{msg}", ResourceBundle.class);
        try {
            if (userSession.getUser() != null) {
                if (userSession.getUser().getUserRole().equals(UserRole.ADMIN)
                        || userSession.getUser().getUserRole().equals(UserRole.STAFF)) {
                    if (category.getParent().getId() != 0) {
                        category.setParent(CategoryDao.readCategory(category));
                    } else {
                        category.getParent().setName(defaultParentCategory);
                    }
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

    private static int generateId() {
        UUID idOne = UUID.randomUUID();
        String str = "" + idOne;
        int uid = str.hashCode();
        String filterStr = "" + uid;
        str = filterStr.replaceAll("-", "");
        return Integer.parseInt(str);
    }
}
