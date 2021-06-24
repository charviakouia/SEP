package de.dedede.model.logic.managed_beans;

import java.awt.*;
import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.net.URL;
import java.util.*;
import java.util.List;

import de.dedede.model.data.dtos.*;
import de.dedede.model.logic.util.MediumType;
import de.dedede.model.persistence.daos.CategoryDao;
import de.dedede.model.persistence.daos.MediumDao;
import de.dedede.model.persistence.exceptions.EntityInstanceNotUniqueException;
import de.dedede.model.persistence.exceptions.LostConnectionException;
import de.dedede.model.persistence.exceptions.MaxConnectionsException;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIInput;
import jakarta.faces.component.UISelectOne;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ValueChangeEvent;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.Part;

import javax.imageio.ImageIO;

/**
 * Backing bean for the medium creator facelet.
 */
@Named
@ViewScoped
public class MediumCreator implements Serializable {

	@Serial private static final long serialVersionUID = 1L;
	private static final int NUM_DISPLAYED_CATEGORY_ENTRIES = 10;
	
	@Inject FacesContext context;

	private MediumDto medium;
	private CopyDto copy;

	private String categorySearchTerm;
	private List<CategoryDto> categories = new LinkedList<>();

	@PostConstruct
	public void init(){
		medium = new MediumDto();
		medium.setReleaseYear(2000);
		copy = new CopyDto();
		copy.setCopyStatus(CopyStatus.AVAILABLE);
		searchForCategory("");
	}

	public MediumDto getMedium() {
		return medium;
	}

	public void setMedium(MediumDto medium) {
		this.medium = medium;
	}

	public CopyDto getCopy() {
		return copy;
	}

	public void setCopy(CopyDto copy) {
		this.copy = copy;
	}

	public List<CategoryDto> getSearchedCategories(){
		return categories;
	}

	public String getCategorySearchTerm() {
		return categorySearchTerm;
	}

	public void setCategorySearchTerm(String categorySearchTerm) {
		this.categorySearchTerm = categorySearchTerm;
	}

	// Navigation methods:

	/**
	 * Creates this medium and its first copy.
	 * @throws EntityInstanceNotUniqueException 
	 * @throws MaxConnectionsException 
	 * @throws LostConnectionException 
	 */
	public String save() throws LostConnectionException, MaxConnectionsException, EntityInstanceNotUniqueException{
		MediumDao.createMedium(medium);
		MediumDao.createCopy(copy, medium);
		ResourceBundle messages =
				context.getApplication().evaluateExpressionGet(context, "#{msg}", ResourceBundle.class);
		context.addMessage("messageForm:positive",
				new FacesMessage(messages.getString("mediumCreator.success")));
		return null;
	}

	public String searchForCategory(){
		UIInput component = (UIInput) context.getViewRoot().findComponent("mediumForm:categorySearchTerm");
		searchForCategory((String) component.getSubmittedValue());
		return null;
	}
	
	private void searchForCategory(String searchTerm) {
		PaginationDto paginationDto = new PaginationDto();
		paginationDto.setPageNumber(0);
		paginationDto.setTotalAmountOfPages(NUM_DISPLAYED_CATEGORY_ENTRIES);
		CategorySearchDto categorySearchDto = new CategorySearchDto();
		categorySearchDto.setSearchTerm(searchTerm);
		categories = CategoryDao.readCategoriesByName(categorySearchDto, paginationDto);
	}

	public String specifyMediumType(){
		UIInput selectOne = (UISelectOne) context.getViewRoot().findComponent("mediumForm:mediumTypeChoice");
		UIInput inputField = (UIInput) context.getViewRoot().findComponent("mediumForm:mediumType");
		MediumType selectedType = MediumType.valueOf((String) selectOne.getSubmittedValue());
		inputField.setSubmittedValue(selectedType.getCanonicalName());
		return null;
	}

	public List<MediumType> getCommonMediumTypes(){
		return List.of(MediumType.values());
	}

	public String getCommonMediumLabel(MediumType type){
		return type.getInternationalName(context);
	}

}
