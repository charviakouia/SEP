package dedede.model.logic.managedbeans;

import java.io.Serializable;

import dto.UserDto;
@Named
@SessionScoped
public class UserSession implements Serializable{
	
	private UserDto userLogedIn;

}
