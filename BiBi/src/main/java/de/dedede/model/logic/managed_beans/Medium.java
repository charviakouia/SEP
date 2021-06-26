package de.dedede.model.logic.managed_beans;

import de.dedede.model.data.dtos.*;
import de.dedede.model.persistence.daos.MediumDao;
import de.dedede.model.persistence.exceptions.*;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

/**
 * Backing bean for the medium facelet. This page provides information about a
 * specific medium and their copies available in the library. Here, users can
 * borrow a copy by committing themselves to pick up the copy from a library
 * site and actually borrowing it there. To trigger the procedure, they can
 * either click on the pick-up button next to a specific copy or on an isolated
 * button which selects an available copy at random. If the parameter is
 * missing, the user will be redirected to the medium search page.
 */
@Named
@ViewScoped
public class Medium extends PaginatedList implements Serializable {

	@Serial
	private static  final long serialVersionUID = 1L;

	private MediumDto mediumDto;

	private CopyDto newCopy;

	private int currentCopyId;

	private String editCopyLocation;

	private String editCopySignature;

	private CopyDto editCopyDto;

	private String signatureForPickUp;

	@Inject
	private UserSession userSession;

	@Inject
	private FacesContext context;

	@Inject
	private Lending lending;

	@Inject
	private ReturnForm returnForm;

	@PostConstruct
	private void init() {
		mediumDto = new MediumDto();
		newCopy = new CopyDto();
		editCopyDto = new CopyDto();
	}

	@Override
	public ArrayList<CopyDto> getItems() {
		return new ArrayList<>(mediumDto.getCopies().values());
	}

	@Override
	public void refresh() {
		onload();
	}

	/**
	 * Used in viewAction to load the page if the passed to the viewPair with the medium id is correct.
	 */
	public void onload() {
		try {
			mediumDto = MediumDao.readMedium(mediumDto);
		} catch (MediumDoesNotExistException e) {
			context.addMessage(null, new FacesMessage("There is no medium with this ID."));
		}
	}

	/**
	 * Save the changes made to the set of medium attributes.
	 *
	 * @throws IOException if the redirect is not possible.
	 */
	public void saveAttributes() throws IOException {
		ResourceBundle messages =
				context.getApplication().evaluateExpressionGet(context, "#{msg}", ResourceBundle.class);
		try {
			MediumDao.updateMedium(mediumDto);
			context.addMessage(null, new FacesMessage(messages.getString("medium.updateSuccess")));
		} catch (EntityInstanceDoesNotExistException e) {
			context.addMessage(null, new FacesMessage(messages.getString("medium.doesntExist")));
			context.getExternalContext().getFlash().setKeepMessages(true);
			FacesContext.getCurrentInstance().getExternalContext().redirect("/BiBi/view/opac/medium-search.xhtml");
		}
	}

	/**
	 * Insert a new copy of this medium.
	 */
	public String createCopy() {
		ResourceBundle messages =
				context.getApplication().evaluateExpressionGet(context, "#{msg}", ResourceBundle.class);
		try {
			prepareNewCopy(newCopy);
			MediumDao.createCopy(newCopy, mediumDto);
			newCopyClean(newCopy);
			context.addMessage(null, new FacesMessage(messages.getString("copy.create.success")));
			refresh();
		} catch (EntityInstanceNotUniqueException exception) {
			context.addMessage(null, new FacesMessage(messages.getString("copy.notUnique")));
		}
		return "";
	}

	private void newCopyClean(CopyDto newCopy) {
		newCopy.setLocation(null);
		newCopy.setId(0);
		newCopy.setSignature(null);
	}

	private void prepareNewCopy(CopyDto newCopy) {
		UUID idOne = UUID.randomUUID();
		String str=""+idOne;
		int uid = str.hashCode();
		String filterStr=""+uid;
		str = filterStr.replaceAll("-", "");
		int newCopyID = Integer.parseInt(str);
		newCopy.setId(newCopyID);
		newCopy.setCopyStatus(CopyStatus.AVAILABLE);
		newCopy.setMediumId(mediumDto.getId());
	}

	/**
	 * Delete a copy of this medium.
	 */
	public void deleteCopy() throws IOException {
		ResourceBundle messages =
				context.getApplication().evaluateExpressionGet(context, "#{msg}", ResourceBundle.class);
		if (mediumDto.getCopy(currentCopyId) == null) {
			context.addMessage(null, new FacesMessage(messages.getString("copy.invalidId")));
			return;
		}
		try {
			MediumDao.deleteCopy(mediumDto.getCopy(currentCopyId));
			cleanEditedAttributes();
			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
			ec.redirect(((HttpServletRequest) ec.getRequest()).getRequestURI()
					+ "?id=" + mediumDto.getId());
			context.addMessage(null, new FacesMessage(messages.getString("copy.del.success")));
		} catch (CopyDoesNotExistException e) {
			context.addMessage(null, new FacesMessage(messages.getString("medium.doesntExist")));
			context.getExternalContext().getFlash().setKeepMessages(true);
			FacesContext.getCurrentInstance().getExternalContext().redirect("/BiBi/view/opac/medium-search.xhtml");
		}
	}

	private void cleanEditedAttributes() {
		editCopySignature = null;
		editCopyLocation = null;
		currentCopyId = 0;
	}

	/**
	 * Save changes made to a copy of this medium.
	 *
	 */
	public void updateCopy() {
		ResourceBundle messages =
				context.getApplication().evaluateExpressionGet(context, "#{msg}", ResourceBundle.class);
		if (mediumDto.getCopy(currentCopyId) == null) {
			context.addMessage(null, new FacesMessage(messages.getString("copy.invalidId")));
			return;
		}
		if (editCopyLocation.isEmpty() || editCopySignature.isEmpty()) {
			context.addMessage(null, new FacesMessage(messages.getString("copy.invalidParam")));
			return;
		}
		try {
			mediumDto.getCopy(currentCopyId).setLocation(editCopyLocation);
			mediumDto.getCopy(currentCopyId).setSignature(editCopySignature);
			MediumDao.updateCopy(mediumDto.getCopy(currentCopyId));
			cleanEditedAttributes();
			refresh();
			context.addMessage(null, new FacesMessage(messages.getString("copy.editSuccess")));
		} catch (CopyDoesNotExistException e) {
			context.addMessage(null, new FacesMessage(messages.getString("copy.doesntExist")));
		} catch (CopyIsNotAvailableException e) {
			context.addMessage(null, new FacesMessage(messages.getString("copy.isNotAvailable")));
		}
	}

	/**
	 * Cancel any pending pickup of the a copy this medium.
	 */
	public void cancelPickup() {
		ResourceBundle messages =
				context.getApplication().evaluateExpressionGet(context, "#{msg}", ResourceBundle.class);
		if (mediumDto.getCopy(currentCopyId) == null
				|| mediumDto.getCopy(currentCopyId).getCopyStatus() != CopyStatus.READY_FOR_PICKUP) {
			context.addMessage(null, new FacesMessage(messages.getString("copy.invalidId")));
			return;
		}
		mediumDto.getCopy(currentCopyId).setActor(null);
		mediumDto.getCopy(currentCopyId).setCopyStatus(CopyStatus.AVAILABLE);
		try {
			MediumDao.updateCopy(mediumDto.getCopy(currentCopyId));
			cleanEditedAttributes();
			refresh();
			context.addMessage(null, new FacesMessage(messages.getString("copy.cancelPickUp.access")));
		} catch (CopyDoesNotExistException e) {
			context.addMessage(null, new FacesMessage(messages.getString("copy.doesntExist")));
		} catch (CopyIsNotAvailableException e) {
			context.addMessage(null, new FacesMessage(messages.getString("copy.isNotAvailable")));
		}
	}

	/**
	 * Go to the direct lending page taking a copy of this medium.
	 */
	public void lendCopy() throws IOException {
		ResourceBundle messages = context.getApplication()
				.evaluateExpressionGet(context, "#{msg}", ResourceBundle.class);
		if (mediumDto.getCopy(currentCopyId) == null) {
			context.addMessage(null, new FacesMessage(messages.getString("copy.invalidId")));
			return;
		}
		if (mediumDto.getCopy(currentCopyId).getCopyStatus() == CopyStatus.BORROWED) {
			context.addMessage(null, new FacesMessage(messages.getString("copy.isBorrowed")));
			return;
		}
		List<CopyDto> copies = new ArrayList<CopyDto>();
		CopyDto copyToLend = mediumDto.getCopy(currentCopyId);
		copies.add(copyToLend);
		lending.setCopies(copies);
		cleanEditedAttributes();
		FacesContext.getCurrentInstance().getExternalContext()
				.redirect("/BiBi/view/staff/lending.xhtml?faces-redirect=true");
	}

	/**
	 * Go to the return form taking a copy of this medium.
	 */
	public void returnCopy() throws IllegalStateException, IOException {
		ResourceBundle messages = context.getApplication()
				.evaluateExpressionGet(context, "#{msg}", ResourceBundle.class);
		if (mediumDto.getCopy(currentCopyId) == null) {
			context.addMessage(null, new FacesMessage(messages.getString("copy.invalidId")));
			return;
		}
		if (mediumDto.getCopy(currentCopyId).getCopyStatus() != CopyStatus.BORROWED) {
			context.addMessage(null, new FacesMessage(messages.getString("copy.isNotBorrowed")));
			return;
		}
		cleanEditedAttributes();
		FacesContext.getCurrentInstance().getExternalContext()
				.redirect("/BiBi/view/staff/return-form.xhtml?userId="
						+ userSession.getUser().getId() + "&copySignature="
						+ mediumDto.getCopy(currentCopyId).getSignature());
	}

	/**
	 * Pick up a copy of this medium.
	 */
	public void pickUpCopy() {
		ResourceBundle messages =
				context.getApplication().evaluateExpressionGet(context, "#{msg}", ResourceBundle.class);
		CopyDto copyDtoForPickUp = new CopyDto();
		copyDtoForPickUp.setSignature(signatureForPickUp);
		try {
			MediumDao.readCopyBySignature(copyDtoForPickUp);
		} catch (EntityInstanceDoesNotExistException exception) {
			context.addMessage(null, new FacesMessage(messages.getString("copy.doesntExist")));
		}
		if (copyDtoForPickUp.getCopyStatus() != CopyStatus.AVAILABLE) {
			context.addMessage(null, new FacesMessage(messages.getString("copy.isNotAvailableForPickup")));
			return;
		}
		mediumDto.getCopy(copyDtoForPickUp.getId()).setActor(userSession.getUser().getId());
		mediumDto.getCopy(copyDtoForPickUp.getId()).setCopyStatus(CopyStatus.READY_FOR_PICKUP);
		try {
			MediumDao.updateCopy(mediumDto.getCopy(copyDtoForPickUp.getId()));
			signatureForPickUp = null;
			refresh();
			context.addMessage(null, new FacesMessage(messages.getString("copy.pickUpWasSuccess")));
		} catch (CopyDoesNotExistException e) {
			context.addMessage(null, new FacesMessage(messages.getString("copy.doesntExist")));
		} catch (CopyIsNotAvailableException e) {
			context.addMessage(null, new FacesMessage(messages.getString("copy.isNotAvailable")));
		}
	}

	public MediumDto getMediumDto() {
		return mediumDto;
	}

	public void setMediumDto(MediumDto mediumDto) {
		this.mediumDto = mediumDto;
	}

	/**
	 * Removes the medium with all its copies.
	 *
	 * @return homepage.
	 */
	public String delete() {
		ResourceBundle messages =
				context.getApplication().evaluateExpressionGet(context, "#{msg}", ResourceBundle.class);
		try {
			MediumDao.deleteMedium(mediumDto);
			context.addMessage(null, new FacesMessage(messages.getString("medium.deleteSuccess")));
			context.getExternalContext().getFlash().setKeepMessages(true);
			return "/view/opac/medium-search?faces-redirect=true";
		} catch (MediumDoesNotExistException e) {
			return "/view/opac/medium-search?faces-redirect=true";
		}
	}

	public CopyDto getNewCopy() {
		return newCopy;
	}

	public void setNewCopy(CopyDto newCopy) {
		this.newCopy = newCopy;
	}

	public UserSession getUserSession() {
		return userSession;
	}

	public void setUserSession(UserSession userSession) {
		this.userSession = userSession;
	}

	public FacesContext getContext() {
		return context;
	}

	public void setContext(FacesContext context) {
		this.context = context;
	}

	public int getCurrentCopyId() {
		return currentCopyId;
	}

	public void setCurrentCopyId(int currentCopyId) {
		this.currentCopyId = currentCopyId;
	}

	public String getEditCopyLocation() {
		return editCopyLocation;
	}

	public void setEditCopyLocation(String editCopyLocation) {
		this.editCopyLocation = editCopyLocation;
	}

	public String getEditCopySignature() {
		return editCopySignature;
	}

	public void setEditCopySignature(String editCopySignature) {
		this.editCopySignature = editCopySignature;
	}

	public CopyDto getEditCopyDto() {
		return editCopyDto;
	}

	public void setEditCopyDto(CopyDto editCopyDto) {
		this.editCopyDto = editCopyDto;
	}

	public String getSignatureForPickUp() {
		return signatureForPickUp;
	}

	public void setSignatureForPickUp(String signatureForPickUp) {
		this.signatureForPickUp = signatureForPickUp;
	}
}
