package de.dedede.model.logic.managed_beans;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import de.dedede.model.data.dtos.CategoryDto;
import de.dedede.model.data.dtos.CategorySearchDto;
import de.dedede.model.data.dtos.MediumDto;
import de.dedede.model.persistence.daos.CategoryDao;
import de.dedede.model.persistence.daos.MediumDao;
import jakarta.annotation.PostConstruct;
import jakarta.faces.component.html.HtmlOutputText;
import jakarta.faces.component.html.HtmlPanelGroup;
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

	// maybe temporary
	// this is the tree o categories … for now … a flat version of it!!
	private List<CategoryDto> categories = CategoryDao.readAllCategoriesTemp();;

	private List<MediumDto> mediums;

	private HtmlPanelGroup categoryTree = new HtmlPanelGroup();

	// initialize the category tree
	{
		categoryTree.setLayout("block");

		forEachCategory(categoryTree, categories);

	}

	// @Temproary, bad name
	private static void forEachCategory(HtmlPanelGroup panel, List<CategoryDto> categories) {
		for (final var subcategory : categories) {
			final var subpanel = new HtmlPanelGroup();

			// @Temp should be a link … kinda commandLink
			final var text = new HtmlOutputText();
			text.setValue(subcategory.getName());

			subpanel.setLayout("block");
			subpanel.setStyle("margin-left: 3rem;");
			subpanel.getChildren().add(text);

			panel.getChildren().add(subpanel);

			forEachCategory(subpanel, subcategory.getChildren());
		}
	}

	@PostConstruct
	public void init() {

	}

	// @Temporary
	public String refr() {
		return "category-browser";
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

	public List<CategoryDto> getCategories() {
		return categories;
	}

	public void setCategories(List<CategoryDto> categories) {
		this.categories = categories;
	}

	public HtmlPanelGroup getCategoryTree() {
		return categoryTree;
	}

	public void setCategoryTree(HtmlPanelGroup categoryTree) {
		this.categoryTree = categoryTree;
	}

	public boolean writableCategoryName() {
//		if (session.getUser() == null) {
//			return false;
//		}
//		
//		return session.getUser().getRole().isStaffOrHigher();

		return true; // @Temporary
	}

	public boolean writableCategoryDescription() {
//		if (session.getUser() == null) {
//			return false;
//		}
//		
//		return session.getUser().getRole().isStaffOrHigher();

		return true; // @Temporary
	}

	public void selectCategory(CategoryDto category) {
		currentCategory = category;

		if (currentCategory != null) {
			mediums = MediumDao.readMediaGivenCategory(currentCategory, getPaginatedList());
		}
	}

	public void saveCategory() {
		CategoryDao.updateCategory(currentCategory);
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
		if (currentCategory != null) {
			mediums = MediumDao.readMediaGivenCategory(currentCategory, getPaginatedList());
		}
	}
}
