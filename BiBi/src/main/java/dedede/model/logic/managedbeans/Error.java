package dedede.model.logic.managedbeans;

import dedede.model.persistence.exceptions.EntityInstanceDoesNotExistException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

@Named
@ApplicationScoped
<<<<<<< HEAD
public class Error {
=======
public class Error extends Page<C> {
>>>>>>> origin/ivansBranch
	
	private EntityInstanceDoesNotExistException entityInstanceDoesNotExistException;
	
	

}
