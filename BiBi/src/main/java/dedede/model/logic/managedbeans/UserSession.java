package dedede.model.logic.managedbeans;

import dedede.model.data.dtos.UserDto;

import java.io.Serializable;

@Named
@SessionScoped
public class UserSession implements Serializable{
	
	private UserDto userLogedIn;

}
