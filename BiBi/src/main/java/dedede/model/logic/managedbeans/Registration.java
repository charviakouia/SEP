package dedede.model.logic.managedbeans;


import dedede.model.data.dtos.UserDto;
import dedede.model.persistence.daos.ApplicationDao;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

@Named
@RequestScoped
<<<<<<< HEAD
public class Registration  {
=======
public class Registration extends Page<C> {
>>>>>>> origin/ivansBranch
	
	
	private ApplicationDao applicationDao;
	
	private UserDto userDto;
	
	private String emailRegex;
	
	@PostConstruct
	public void init() {
		
	}
	
	public void register() {
		
	}
}
