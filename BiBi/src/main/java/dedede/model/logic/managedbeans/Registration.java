package dedede.model.logic.managedbeans;


import dedede.model.data.dtos.UserDto;
import dedede.model.persistence.daos.ApplicationDao;

import javax.annotation.PostConstruct;

@Named
@RequestScoped
public class Registration  {
	
	
	private ApplicationDao applicationDao;
	
	private UserDto userDto;
	
	private String emailRegex;
	
	@PostConstruct
	public void init() {
		
	}
	
	public void register() {
		
	}
}
