package dedede.model.logic.managedbeans;

import dedede.model.logic.util.LocaleBundle;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
  * Backing bean for the header facelet of the application. Implements functions
  * necessary for the header facelet  into the template.
 */

@Named
@RequestScoped
public class Header {

    @Inject
    private LocaleBundle localeBundle;

    @Inject
    private FacesContext facesContext;

    /**
     * It will be called when the user clicks on the logout button. The current session of the
     * user is invalidated and the user is lead back to the {@link Login}.
     * @return   the String to the login and Register Page.
     */
    public  String logout() {
        return "";
    }


    /**
     * Sets the language  of this program
     * @param language   the language you would set
     */
    public void setLanguage(String language){

    }
}
