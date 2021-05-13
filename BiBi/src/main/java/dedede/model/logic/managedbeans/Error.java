package dedede.model.logic.managedbeans;

import dedede.model.persistence.exceptions.EntityInstanceDoesNotExistException;

@Named
@ApplicationScoped
public class Error extends Page<C> {
	
	private EntityInstanceDoesNotExistException entityInstanceDoesNotExistException;
	
	

}
