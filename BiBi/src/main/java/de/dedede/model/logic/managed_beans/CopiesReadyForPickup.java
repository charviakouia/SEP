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
import de.dedede.model.logic.util.MessagingUtility;
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
 * Backing bean for the facelet contain a list of copies ready for pickup for
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

	/**
	 * Loads a page with a list of copies according to the given viewParam.
	 * If the user is not logged in or sends a viewParam from someone other than his own,
	 * the page will not be loaded and the user will see an error message.
	 *
	 * @throws BusinessException If the redirect is not possible.
	 */
	public void onload() throws BusinessException {
		try {
			if (userSession.getUser() != null) {
				if (userSession.getUser().getId() == userDto.getId()) {
					copies = MediumDao.readMarkedCopiesByUser(getPaginatedList(), userDto);
				} else {
					MessagingUtility.writeNegativeMessageWithKey(context, true, "profile.notAccess");
					FacesContext.getCurrentInstance().getExternalContext()
							.redirect("/BiBi/view/opac/medium-search.xhtml");
				}
			} else {
				MessagingUtility.writeNegativeMessageWithKey(context, true, "profile.notLogin");
				FacesContext.getCurrentInstance().getExternalContext()
						.redirect("/BiBi/view/ffa/login.xhtml");
			}
		} catch (IOException e) {
			throw new BusinessException("Redirect is not possible.");
		}
	}

	/**
	 * @return Fetches a list of copies that the user can pick up.
	 */
	public List<MediumCopyUserDto> getCopies() {
		return copies;
	}

	/**
	 * Sets a list of copies that the user can pick up.
	 * @param copies a list of copies
	 */
	public void setCopies(List<MediumCopyUserDto> copies) {
		this.copies = copies;
	}

	/**
	 * @return Fetches the userDto for which the copy list will be loaded, ready for pick up.
	 */
	public UserDto getUserDto() {
		return userDto;
	}

	/**
	 * Sets the userDto for which the copy list will be loaded, ready for pick up.
	 * @param userDto container
	 */
	public void setUserDto(UserDto userDto) {
		this.userDto = userDto;
	}
}