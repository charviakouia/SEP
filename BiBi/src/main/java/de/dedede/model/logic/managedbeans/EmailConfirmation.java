package de.dedede.model.logic.managedbeans;

import java.io.Serial;
import java.io.Serializable;

import de.dedede.model.data.dtos.TokenDto;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
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

	@Serial
	private static final long serialVersionUID = 1L;

	private TokenDto token;

	@PostConstruct
	public void init() {

	}

	public TokenDto getToken() {
		return token;
	}

	public void setToken(TokenDto token) {
		this.token = token;
	}

}
