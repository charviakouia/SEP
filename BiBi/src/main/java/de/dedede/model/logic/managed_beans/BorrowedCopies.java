package de.dedede.model.logic.managed_beans;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;

import de.dedede.model.data.dtos.CopyDto;
import de.dedede.model.data.dtos.MediumCopyUserDto;
import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.logic.exceptions.BusinessException;
import de.dedede.model.persistence.daos.MediumDao;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Backing bean for the page containing a list of borrowed copies of the current
 * user. This page informs the user which copies they borrowed from the library
 * and how much time they still have left until they have to return them to not
 * exceed the lending period.
 */
@Named
@SessionScoped
public class BorrowedCopies extends PaginatedList implements Serializable {

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
	 */
	public void onload() throws BusinessException {
		ResourceBundle messages =
				context.getApplication().evaluateExpressionGet(context, "#{msg}", ResourceBundle.class);
		try {
			if (userSession.getUser() != null) {
				if (userSession.getUser().getId() == userDto.getId()) {
					copies = MediumDao.readLentCopiesByUser(getPaginatedList(), userDto);
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