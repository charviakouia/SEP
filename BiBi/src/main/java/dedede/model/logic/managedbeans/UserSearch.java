package dedede.model.logic.managedbeans;

import java.io.Serializable;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

@Named
@RequestScoped
public class UserSearch extends PaginatedList implements Serializable {
	
	
	
	private String searchValue;
	
	
	
	public void userSearch () {
		
	}
	

	
}
