package dedede.model.logic.managedbeans;


import javax.annotation.PostConstruct;

import dao.ApplicationDao;
import dao.UserDao;
import dto.UserDto;

@Named
@RequestScoped
public class Registration extends Page {
	
	
	private ApplicationDao applicationDao;
	
	private UserDto userDto;
	
	private String emailRegex;
	
	@PostConstruct
	public void init() {
		
	}
	
	public void register() {
		
	}
}
