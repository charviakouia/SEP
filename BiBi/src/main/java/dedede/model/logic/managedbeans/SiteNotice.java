package dedede.model.logic.managedbeans;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

@Named
@RequestScoped
<<<<<<< HEAD
public class SiteNotice  {
=======
public class SiteNotice extends Page<C> {
>>>>>>> origin/ivansBranch
	
	
	private String siteNotice;
	
	public void save() {
		
	}

}
