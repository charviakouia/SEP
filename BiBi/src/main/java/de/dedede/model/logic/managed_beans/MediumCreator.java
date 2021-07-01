package de.dedede.model.logic.managed_beans;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.List;

import de.dedede.model.data.dtos.*;
import de.dedede.model.logic.exceptions.BusinessException;
import de.dedede.model.logic.util.MediumType;
import de.dedede.model.logic.util.MessagingUtility;
import de.dedede.model.persistence.daos.CategoryDao;
import de.dedede.model.persistence.daos.MediumDao;
import de.dedede.model.persistence.exceptions.EntityInstanceNotUniqueException;
import de.dedede.model.persistence.exceptions.LostConnectionException;
import de.dedede.model.persistence.exceptions.MaxConnectionsException;

import de.dedede.model.persistence.util.Logger;
import jakarta.annotation.PostConstruct;
import jakarta.faces.component.UIInput;
import jakarta.faces.component.UISelectOne;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Backing bean for the medium-creator facelet. This facelet allows staff and administrators to
 * create medium-entities, which also requires the creation of one corresponding medium-copy entity.
 *
 * @author Ivan Charviakou
 */
@Named
@ViewScoped
public class MediumCreator implements Serializable {

	@Serial private static final long serialVersionUID = 1L;
	private static final int NUM_DISPLAYED_CATEGORY_ENTRIES = 10;
	
	@Inject FacesContext context;
	private List<CategoryDto> categories = new LinkedList<>();

	private MediumDto medium;
	private CopyDto copy;
	private String categorySearchTerm;

	/**
	 * Initializes the backing bean and sets default values for the medium, medium-copy, and displayed categories.
	 * In particular, the maximum number of displayable categories is loaded.
	 */
	@PostConstruct
	public void init(){
		medium = new MediumDto();
		medium.setReleaseYear(2000);
		copy = new CopyDto();
		copy.setCopyStatus(CopyStatus.AVAILABLE);
		searchForCategory("");
	}

	/**
	 * Updates the displayed list of categories based on the entered search term. As the results are not paginated,
	 * only a certain number of entries are considered from the returned dataset. An empty search term returns
	 * the whole dataset.
	 */
	public void searchForCategory(){
		UIInput component = (UIInput) context.getViewRoot().findComponent("mediumForm:categorySearchTerm");
		searchForCategory((String) component.getSubmittedValue());
	}

	private void searchForCategory(String searchTerm) {
		PaginationDto paginationDto = new PaginationDto();
		paginationDto.setPageNumber(0);
		paginationDto.setTotalAmountOfPages(NUM_DISPLAYED_CATEGORY_ENTRIES);
		CategorySearchDto categorySearchDto = new CategorySearchDto();
		categorySearchDto.setSearchTerm(searchTerm);
		try {
			categories = CategoryDao.readCategoriesByName(categorySearchDto, paginationDto);
		} catch (LostConnectionException e){
			MessagingUtility.writeNegativeMessageWithKey(context, false, "mediumCreator.error.categoryReadFailed");
		}
	}

	/**
	 * Creates the specified medium and its first copy.
	 *
	 * @throws BusinessException Is thrown when the medium or medium-copy couldn't be created. This can have
	 * 		several causes. On one hand, this can occur if the medium-copy's signature is already present in
	 * 		the datastore. As this field is validated beforehand, this is the result of race-condition. On the
	 * 		other hand, this can occur if the maximum number connections has been reached while processing
	 * 		the entity-creation statements or the given connection has been damaged while processing these
	 * 		statements.
	 */
	/**
	 * Creates the specified medium and its first copy.
	 *
	 * @return Navigation to the details page for the created medium.
	 * @throws BusinessException Is thrown when the medium or medium-copy couldn't be created. This can have
	 * 		several causes. On one hand, this can occur if the medium-copy's signature is already present in
	 * 		the datastore. As this field is validated beforehand, this is the result of race-condition. On the
	 * 		other hand, this can occur if the maximum number connections has been reached while processing
	 * 		the entity-creation statements or the given connection has been damaged while processing these
	 * 		statements.
	 */
	public String save() throws BusinessException {
		try {
			MediumDao.createMedium(medium);
			MediumDao.createCopy(copy, medium);
			MessagingUtility.writePositiveMessageWithKey(context, false, "mediumCreator.success");
			return "/view/opac/medium.xhtml?faces-redirect=true&id=" + medium.getId();
		} catch (LostConnectionException | MaxConnectionsException e){
			String msg = "Datastore error occurred - cannot create medium or medium-copy entities";
			Logger.severe(msg);
			throw new BusinessException(msg, e);
		} catch (EntityInstanceNotUniqueException e){
			String msg = "Medium-copy entity with signature: " + copy.getSignature() + " already exists";
			Logger.severe(msg);
			throw new BusinessException(msg, e);
		}
	}

	/**
	 * Inputs the generic medium-type selected by the user into the corresponding input field. The used value
	 * corresponds to the type's canonical value.
	 */
	public void specifyMediumType(){
		UIInput selectOne = (UISelectOne) context.getViewRoot().findComponent("mediumForm:mediumTypeChoice");
		MediumType selectedType = MediumType.valueOf((String) selectOne.getSubmittedValue());
		UIInput inputField = (UIInput) context.getViewRoot().findComponent("mediumForm:mediumType");
		inputField.setSubmittedValue(selectedType.getCanonicalName());
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

	public List<MediumType> getCommonMediumTypes(){
		return List.of(MediumType.values());
	}

	public String getCommonMediumLabel(MediumType type){
		return type.getInternationalName(context);
	}

}
