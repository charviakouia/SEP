package dedede.model.logic.managedbeans;

import dedede.model.persistence.exceptions.EntityInstanceDoesNotExistException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

@Named
@ApplicationScoped
public class Error {

	private EntityInstanceDoesNotExistException entityInstanceDoesNotExistException;

}
