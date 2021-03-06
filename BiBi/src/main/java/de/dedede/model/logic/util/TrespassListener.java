package de.dedede.model.logic.util;

import java.io.Serial;
import java.io.Serializable;
import java.util.ResourceBundle;

import de.dedede.model.data.dtos.UserRole;
import de.dedede.model.logic.managed_beans.ApplicationCustomization;
import de.dedede.model.logic.managed_beans.UserSession;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.faces.application.Application;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.application.NavigationHandler;
import jakarta.faces.component.UIViewRoot;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.PhaseEvent;
import jakarta.faces.event.PhaseId;
import jakarta.faces.event.PhaseListener;
import jakarta.inject.Inject;

/**
 * Access control for the application is done here. Loginstatus, system access 
 * mode and user role are matched against the requested url-pattern and access 
 * is restricted if user rights are insufficient. Finer security involving 
 * view-parameters is done in the respective backing beans.
 * 
 * @author Jonas Picker
 */
public class TrespassListener implements PhaseListener, Serializable{ 
	
	@Serial
	private static final long serialVersionUID = 1L;
	
	/**
	 * The user data corresponding to the request source injected manually.
	 */
	@Inject
	private UserSession userSession = 
					CDI.current().select(UserSession.class).get();
	
	/**
	 * The applicationscoped managed bean that holds the database customizations
	 * in order to prevent system access mode queries for each request.
	 */
	@Inject
	private ApplicationCustomization customs = 
					CDI.current().select(ApplicationCustomization.class).get();
		
	/**
	 * The processes the TrespassListener performs after the phase is executed.
	 * Filters url-patterns against the users login-status, role and the systems
	 * access mode. Open/closed registration access control should be handled in
	 * BB to enable admins to still create new accounts.
	 *
	 * @param phaseEvent The event on which the listener is triggered.
	 */
	@Override
	public void afterPhase(PhaseEvent phaseEvent) {
        FacesContext facesCtx = phaseEvent.getFacesContext();
        ExternalContext externalCtx = facesCtx.getExternalContext(); 
        Application app = facesCtx.getApplication();
        NavigationHandler navigationHandler = app.getNavigationHandler();
		ResourceBundle messages = app.evaluateExpressionGet(facesCtx, 
				"#{msg}", ResourceBundle.class);
		UIViewRoot viewRoot = facesCtx.getViewRoot();  
        String url = viewRoot.getViewId();
        boolean isLoggedIn = false;
        UserRole userRole = null;
        if (userSession != null && userSession.getUser() != null) {
        	isLoggedIn = true;
        	userRole = userSession.getUser().getRole();
        }
    	String shortMessageLogin = messages.getString("trespassListener.login"
    			+ "_or_register_short");
    	String longMessageLogin = messages.getString("trespassListener.login"
    			+ "_or_register_long");
        SystemAnonAccess accessMode 
        				= customs.getApplicationCustomization().getAnonRights();
        if (!isLoggedIn && !url.startsWith("/view/ffa/") 
        	&& !url.startsWith("/view/error/")
        	&& (accessMode == SystemAnonAccess.REGISTRATION)) {
        	redirectToLogin(facesCtx, externalCtx, navigationHandler,
        			shortMessageLogin, longMessageLogin);
        } else if (!isLoggedIn && !url.startsWith("/view/ffa/") 
        			&& !url.startsWith("/view/opac/") 
        			&& !url.startsWith("/view/error/")
        			&& (accessMode == SystemAnonAccess.OPAC)) {
        	redirectToLogin(facesCtx, externalCtx, navigationHandler,
        				shortMessageLogin, longMessageLogin);
        } else if (isLoggedIn && !url.startsWith("/view/ffa/")
        			&& (userRole == UserRole.REGISTERED)) {
        	if (!url.startsWith("/view/opac/") 
        		&& !url.startsWith("/view/error/")
        		&& !url.startsWith("/view/account/")) {
        		redirectToError404(facesCtx, externalCtx, navigationHandler);
        	}
        } else if (isLoggedIn && !url.startsWith("/view/ffa/") 
        			&& (userRole == UserRole.STAFF)) {
        	if(!url.startsWith("/view/opac/") 
        		&& !url.startsWith("/view/account/") 
        		&& !url.startsWith("/view/staff/")
        		&& !url.startsWith("/view/error/")) {
        		redirectToError404(facesCtx, externalCtx, navigationHandler);
        	}
        }      
	}

	private void redirectToError404(FacesContext facesCtx,
			ExternalContext externalCtx,
			NavigationHandler navigationHandler) {
		navigationHandler.handleNavigation(facesCtx, null,
				"/view/error/error404.xhtml?faces-redirect=true");
		facesCtx.responseComplete();
	}

	private void redirectToLogin(FacesContext facesCtx,
			ExternalContext externalCtx, NavigationHandler navigationHandler,
			String shortMessage, String longMessage) {
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
				shortMessage, longMessage);
		facesCtx.addMessage(null, msg);
		externalCtx.getFlash().setKeepMessages(true);
		navigationHandler.handleNavigation(facesCtx, null,
				"/view/ffa/login.xhtml?faces-redirect=true");          
		facesCtx.responseComplete();
	}
	
	/**
	 * Returns the phase in which the TrespassListener gets active.
	 *
	 * @return the restore-view phase of the jsf lifecycle
	 */
	@Override
	public PhaseId getPhaseId() {
		
        return PhaseId.RESTORE_VIEW;
	}
	
	/**
	 * The processes the TrespassListener performs before the phase is executed. 
	 * In this case the method is empty.
	 *
	 * @param phaseEvent The event on which the listener is triggered.
	 */
	@Override
	public void beforePhase(PhaseEvent phaseEvent) {}
	
}
