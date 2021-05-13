package dedede.model.logic.managedbeans;

import dedede.model.persistence.daos.MediumDao;
import dedede.model.persistence.daos.UserDao;

@Named
@ViewScoped
public class MediumCreation extends Page<C> {
	
	private MediumDao mediumDao;
	
	private UserDao userDao;
	
	
	
	public void createMediumAndFirstCopy() {
		
	}

}
