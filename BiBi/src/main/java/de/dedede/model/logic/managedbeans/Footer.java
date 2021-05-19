package de.dedede.model.logic.managedbeans;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * The footer has a text displayed for the user to see his login status with his
 * name.
 */

@Named
@RequestScoped
public class Footer {

	@Inject
	private UserSession userSession;

	/**
	 * A text is displayed as which user you are signed in.
	 *
	 * @return an empty String if you are not signed in, the text with your name
	 *         otherwise.
	 */
	public String getNAmeSignedInUser() {
		return null;
	}
}
