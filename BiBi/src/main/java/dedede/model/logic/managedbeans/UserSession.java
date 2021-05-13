package dedede.model.logic.managedbeans;

import java.io.Serializable;

import dedede.model.data.dtos.UserDto;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

/**
 * Managed bean for the user session containing the logged-in user.
 */
@Named
@SessionScoped
public class UserSession implements Serializable {

	private UserDto loggedInUser;

}
