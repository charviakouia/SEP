package dedede.model.logic.managedbeans;

import dao.UserDao;
import dedede.model.logic.managedbeans.Profile;
import dto.UserDto;
import validator.EmailValidator;
import validator.PasswordValidator;

@Named
@ViewScoped
public class Profile extends Page {
	
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
