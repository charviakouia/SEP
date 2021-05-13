package dedede.model.logic.managedbeans;


import dedede.model.data.dtos.UserDto;
import dedede.model.persistence.daos.ApplicationDao;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

//@Named
//@RequestScoped
public class Registration<C> extends Page<C> {
	
	
	private ApplicationDao applicationDao;
	
	private UserDto userDto;
	
	private String emailRegex;
	
	@PostConstruct
	public void init() {
		
	}
	
	public void register() {
		
	}
}
