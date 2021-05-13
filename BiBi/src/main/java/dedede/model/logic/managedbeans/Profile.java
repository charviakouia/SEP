package dedede.model.logic.managedbeans;

import dedede.model.data.dtos.UserDto;
import dedede.model.logic.validators.EmailValidator;
import dedede.model.logic.validators.PasswordValidator;
import dedede.model.persistence.daos.UserDao;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

@Named
@ViewScoped
public class Profile {
	
	private UserDao user;
	
	private UserDto userDto;
	
	private String password;
	
	private String reapeatPasword;
	
	private String email;
	
	private String userId;
	
	private EmailValidator emailValidator;
	
	private PasswordValidator passwordValidator;
	
	private UserSession userSession;

	private Profile profile1;

	private PasswordReset passwordReset1;
	
	
	public void delete() {
		
	}
	
    
    public void save() {
    	
    }
}
