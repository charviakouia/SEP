package dedede.model.logic.managedbeans;

import dedede.model.persistence.exceptions.MaxConnectionsException;
import dedede.model.persistence.util.Logger;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named
@RequestScoped
public class Login {
 	private static final Logger logger = null;

 	@Inject
	private FacesContext facesContext;

 	@Inject
	private  UserSession userSession;

	private String email;

	private String password;


	@PostConstruct
	public void init(){

	}
	void login()  throws MaxConnectionsException {

	}

	public void forgotPassword() {

	}

}
