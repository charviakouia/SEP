package de.dedede.model.logic.managed_beans;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.dedede.model.data.dtos.CategoryBrowserColumn;
import de.dedede.model.data.dtos.CategoryDto;
import de.dedede.model.data.dtos.CategorySearchDto;
import de.dedede.model.data.dtos.MediumDto;
import de.dedede.model.persistence.daos.CategoryDao;
import de.dedede.model.persistence.daos.MediumDao;
import de.dedede.model.persistence.util.Logger;
import jakarta.annotation.PostConstruct;
import jakarta.el.ELContext;
import jakarta.el.ExpressionFactory;
import jakarta.faces.component.html.HtmlCommandLink;
import jakarta.faces.component.html.HtmlForm;
import jakarta.faces.component.html.HtmlPanelGroup;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Backing bean for the category browser facelet. This page offers users to
 * browse through all available categories mediums are part of in a structured
 * manner. It renders it possible for the consumer to discover new mediums.
 */
@Named
@ViewScoped
public class CategoryBrowser extends PaginatedList<CategoryBrowserColumn> implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;
	
	@Inject
	private FacesContext ctx;

	@Inject
	private ExternalContext ectx;

	private ELContext elctx;

	private ExpressionFactory expr;

	@Inject
	private UserSession session;

	private CategorySearchDto categorySearch = new CategorySearchDto();

	private CategoryDto currentCategory;

	private List<CategoryDto> categories;

	private List<MediumDto> mediums;

	private HtmlPanelGroup categoryTreeHook = new HtmlPanelGroup();

	private int previousCategoryTreeId = 0;

	private static HtmlPanelGroup createContainer(String styleClass) {
		final var component = new HtmlPanelGroup();
		component.setLayout("block");
		component.setStyleClass(styleClass);
		return component;
	}

	/**
	 * Initialize this backing bean.
	 * 
	 * This method is not meant to be called directly but only by JSF and that only
	 * once!
	 */
	@PostConstruct
	public void init() {
		categories = CategoryDao.readCategoryForest();
		expr = ctx.getApplication().getExpressionFactory();
		elctx = ctx.getELContext();
		categoryTreeHook.getChildren().add(bundleCategoryForest(categories));
	}

	private HtmlPanelGroup bundleCategoryForest(List<CategoryDto> categories) {

		final var tree = createContainer("accordion accordion-flush");
		final var treeId = (previousCategoryTreeId += 1);
		final var qualifiedTreeId = "category_tree_" + treeId;
		tree.setId(qualifiedTreeId);

		for (final var category : categories) {
			final var categoryNameLink = new HtmlCommandLink();
			categoryNameLink.setId("link_category_name_" + category.getId());
			categoryNameLink.setValue(category.getName());
			elctx.getVariableMapper().setVariable("category", expr.createValueExpression(category, CategoryDto.class));
			categoryNameLink.setActionExpression(expr.createMethodExpression(elctx,
					"#{categoryBrowser.selectCategory(category)}", Void.class, new Class<?>[] { CategoryDto.class }));

			final var qualifiedAccordionCollapseId = "accordion_collapse_" + category.getId();
			final var accordionButton = new HtmlForm();
			{
				final var styleClass = new StringBuilder();
				styleClass.append("accordion-button collapsed");

				if (category.getChildren().isEmpty()) {
					styleClass.append(" disabled");
				} else {
					accordionButton.getPassThroughAttributes().put("data-bs-toggle", "collapse");
				}

				accordionButton.getChildren().add(categoryNameLink);
				accordionButton.getPassThroughAttributes().put("data-bs-target", "#" + qualifiedAccordionCollapseId);
				accordionButton.setStyleClass(styleClass.toString());
			}
			
			final var accordionHeader = createContainer("accordion-header");
			accordionHeader.getChildren().add(accordionButton);

			final var accordionBody = createContainer("accordion-body ps-3 pe-0 py-2");

			final var accordionCollapse = createContainer("accordion-collapse collapse");
			accordionCollapse.setId(qualifiedAccordionCollapseId);
			accordionCollapse.getPassThroughAttributes().put("data-bs-parent", "#" + qualifiedTreeId);
			accordionCollapse.getChildren().add(accordionBody);

			final var accordionItem = createContainer("accordion-item");
			accordionItem.getChildren().add(accordionHeader);
			accordionItem.getChildren().add(accordionCollapse);

			tree.getChildren().add(accordionItem);

			accordionBody.getChildren().add(bundleCategoryForest(category.getChildren()));
		}

		return tree;
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

	public HtmlPanelGroup getCategoryTreeHook() {
		return categoryTreeHook;
	}

	public void setCategoryTreeHook(HtmlPanelGroup categoryTreeHook) {
		this.categoryTreeHook = categoryTreeHook;
	}

	public Collection<CategoryDto> getCurrentCategoryPath() {
		final var TYPICAL_LONGEST_PATH_LENGTH = 3;
		final var path = new ArrayDeque<CategoryDto>(TYPICAL_LONGEST_PATH_LENGTH);
		
		for (var category = currentCategory; category != null; category = category.getParent()) {
			path.addFirst(category);
		}

		return path;
	}

	public boolean writableCategoryControls() {
		if (session.getUser() == null) {
			return false;
		}

		return session.getUser().getRole().isStaffOrHigher();
	}

	/**
	 * Dictates whether category-modifying actions are displayed to the user.
	 * 
	 * @return If the category actions are shown.
	 */
	public boolean isShowingCategoryActions() {
		if (session.getUser() == null) {
			return false;
		}

		return session.getUser().getRole().isStaffOrHigher();
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

	public String deleteCategory() {
		CategoryDao.deleteCategory(currentCategory);
		currentCategory = null;

		return "category-browser";
	}

	public String searchCategories() {
		
		return "category-browser";
	}

	public void createCategory() throws IOException {
		ectx.getFlash().put("parent-category", currentCategory.getId());
		ectx.redirect(ectx.getRequestContextPath() + "/view/staff/category-creator.xhtml");
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
