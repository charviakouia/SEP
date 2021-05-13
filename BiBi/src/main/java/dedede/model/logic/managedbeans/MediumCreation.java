package dedede.model.logic.managedbeans;

import dedede.model.persistence.daos.MediumDao;
import dedede.model.persistence.daos.UserDao;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

@Named
@ViewScoped
public class MediumCreation {
	
	private MediumDao mediumDao;
	
	private UserDao userDao;

	
	public void createMediumAndFirstCopy() {
		
	}

}
