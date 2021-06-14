package de.dedede.model.logic.managed_beans;

import java.io.Serial;
import java.io.Serializable;

import de.dedede.model.data.dtos.TokenDto;
import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.logic.util.UserVerificationStatus;
import de.dedede.model.persistence.daos.UserDao;
import de.dedede.model.persistence.exceptions.EntityInstanceDoesNotExistException;
import de.dedede.model.persistence.exceptions.UserDoesNotExistException;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Backing bean for the email confirmation page. Accessing this page potentially
 * verifies the email address of a specific user. For this, it takes a token as
 * a query parameter. If absent or invalid, nothing user-facing happens. This
 * secures against certain attacks.
 */
@Named
@RequestScoped
public class EmailConfirmation implements Serializable {

	@Serial private static final long serialVersionUID = 1L;
	
	@Inject private FacesContext context;

	private TokenDto token;
	private UserDto user;

	@PostConstruct
	public void init() {
		token = new TokenDto();
		user = new UserDto();
	}

	public TokenDto getToken() {
		return token;
	}

	public void setToken(TokenDto token) {
		this.token = token;
	}
	
	public String attemptToConfirm() throws UserDoesNotExistException, EntityInstanceDoesNotExistException {
		user.setToken(token);
		UserDao.readUserByToken(user);
		user.setUserVerificationStatus(UserVerificationStatus.VERIFIED);
		user.setToken(null);
		user.setTokenCreation(null);
		UserDao.updateUser(user);
		context.addMessage(null, new FacesMessage("Successfully verified email"));
		context.getExternalContext().getFlash().setKeepMessages(true);
		return "/view/account/profile.xhtml?faces-redirect=true";
	}

}
