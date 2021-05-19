package de.dedede.model.logic.managedbeans;

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

import java.io.Serializable;

/**
 * Backing bean for the email confirmation page. Accessing this page potentially
 * verifies the email address of a specific user. For this, it takes a token as
 * a query parameter. If absent or invalid, nothing user-facing happens. This
 * secures against certain attacks.
 */
@Named
@RequestScoped
public class EmailConfirmation implements Serializable {

	private String token;

}
