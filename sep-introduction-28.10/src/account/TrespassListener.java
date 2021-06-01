package account;

import dao.LoggedInUser;

import javax.enterprise.inject.spi.CDI;
import javax.faces.application.NavigationHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

/**
 * Checks requests on user authentication.
 */

public class TrespassListener implements PhaseListener {

    private static final long serialVersionUID = 1L;

    private final LoggedInUser loggedInUser = CDI.current().select(LoggedInUser.class).get();

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
    }

    @Override
    public void beforePhase(PhaseEvent event) {
    }

    @Override
    public void afterPhase(PhaseEvent event) {
        FacesContext fctx = event.getFacesContext();

        // Is the user on the login page?
        boolean publicArea = false;
        UIViewRoot viewRoot = fctx.getViewRoot();
        if (viewRoot != null) {
            String url = viewRoot.getViewId();
            publicArea = url.endsWith("login.xhtml");
        }

        // Is the user already authenticated?
        boolean loggedIn = loggedInUser != null && loggedInUser.getUser() != null;

        if (!publicArea && !loggedIn) {
            // Illegal request.
            // Redirect to the login page.
            NavigationHandler nav = fctx.getApplication().getNavigationHandler();
            nav.handleNavigation(fctx, null, "login.xhtml?faces-redirect=true");

        }
    }

}
//-----------------------------------------------------------------------------
//end of file
//-----------------------------------------------------------------------------
