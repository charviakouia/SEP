package account;
import dao.LoggedInUser;
import models.User;
import dao.SqlUserDb;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ResourceBundle;

@Named("accountDetails")
@RequestScoped
public class UserAccount {

    @Inject
    private FacesContext context;

    @Inject
    private LoggedInUser user;

    @Inject
    @Named("sqlUserDatabase")
    private SqlUserDb userDao;

    private final ResourceBundle messages = ResourceBundle.getBundle("resources/messages");

    public User getUser() {
        return user.getUser();
    }

    public void saveChanges() {
        if (userDao.userNameAlreadyTaken(user.getUser())) {
            FacesMessage msg = new FacesMessage(messages.getString("usernameTaken"));
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            context.addMessage("account:username", msg);
        } else {
            boolean success = userDao.updateUser(user.getUser());

            if (success) {
                FacesMessage msg = new FacesMessage(messages.getString("savedChanges"));
                msg.setSeverity(FacesMessage.SEVERITY_INFO);
                context.addMessage("account", msg);
            } else {
                FacesMessage msg = new FacesMessage(messages.getString("savingFailed"));
                msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                context.addMessage("account", msg);
            }
        }
    }
}
