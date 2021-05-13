package dedede.model.logic.managedbeans;

import dedede.model.persistence.daos.MediumDao;
import dedede.model.persistence.daos.UserDao;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

@Named
@ViewScoped
<<<<<<< HEAD
public class MediumCreation {
=======
public class MediumCreation extends Page<C> {
>>>>>>> origin/ivansBranch
	
	private MediumDao mediumDao;
	
	private UserDao userDao;

	
	public void createMediumAndFirstCopy() {
		
	}

}
