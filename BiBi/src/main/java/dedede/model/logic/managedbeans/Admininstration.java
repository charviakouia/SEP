package dedede.model.logic.managedbeans;

import dedede.model.data.dtos.ApplicationDto;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named
@SessionScoped
public class Admininstration {
	
	@Inject
	private ApplicationDto applicationDto;
	
	public void save() {
		
	}
	public void searchUser() {
		
	}

}
