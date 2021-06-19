package de.dedede.model.logic.managed_beans;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;

import de.dedede.model.data.dtos.MediumCopyUserDto;
import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.data.dtos.UserRole;
import de.dedede.model.logic.exceptions.BusinessException;
import de.dedede.model.persistence.daos.MediumDao;
import de.dedede.model.persistence.daos.UserDao;
import de.dedede.model.persistence.exceptions.UserDoesNotExistException;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Backing bean for the facelet containg a list of copies ready for pickup for
 * the current user. On this page a user gets to know which copies they want to
 * pick up from the library and borrow thereafter and how much time they have
 * left to do so until they exceed the lending period.
 */
@Named
@SessionScoped
public class CopiesReadyForPickup extends PaginatedList implements Serializable {

	@Serial
	private static  final long serialVersionUID = 1L;

	private List<MediumCopyUserDto> copies;

	private UserDto userDto;

	@Inject
	UserSession userSession;

	@Inject
	private FacesContext context;

	@PostConstruct
	public void init() {
		userDto = new UserDto();
	}

	@Override
	public List<MediumCopyUserDto> getItems() {
		return copies;
	}

	@Override
	public void refresh() {
		onload();
	}

	public void onload() throws BusinessException {
		ResourceBundle messages =
				context.getApplication().evaluateExpressionGet(context, "#{msg}", ResourceBundle.class);
		try {
			if (userSession.getUser() != null) {
				if (userSession.getUser().getId() == userDto.getId()) {
					copies = MediumDao.readMarkedCopiesByUser(getPaginatedList(), userDto);
				} else {
					context.addMessage(null, new FacesMessage(messages.getString("profile.notAccess")));
					context.getExternalContext().getFlash().setKeepMessages(true);
					FacesContext.getCurrentInstance().getExternalContext()
							.redirect("/BiBi/view/public/medium-search.xhtml");
				}
			} else {
				context.addMessage(null, new FacesMessage(messages.getString("profile.notLogin")));
				context.getExternalContext().getFlash().setKeepMessages(true);
				FacesContext.getCurrentInstance().getExternalContext()
						.redirect("/BiBi/view/public/login.xhtml");
			}
		} catch (IOException e) {
			throw new BusinessException("Redirect is not possible.");
		}
	}

	public List<MediumCopyUserDto> getCopies() {
		return copies;
	}

	public void setCopies(List<MediumCopyUserDto> copies) {
		this.copies = copies;
	}

	public UserDto getUserDto() {
		return userDto;
	}

	public void setUserDto(UserDto userDto) {
		this.userDto = userDto;
	}
}