package account;

import dao.LoggedInUser;
import dao.SqlUserDb;
import models.User;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Optional;
import java.util.ResourceBundle;

@Named("login")
@RequestScoped
public class Login {

    @Inject
    private FacesContext context;

    private String userName = "";

    private String password = "";

    @Inject
    private LoggedInUser loggedInUser;

    @Inject
    @Named("sqlUserDatabase")
    private SqlUserDb userDao;

    private final ResourceBundle messages = ResourceBundle.getBundle("resources/messages");

    public String login() {
        Optional<User> user = userDao.getUser(userName, password);

        if (user.isPresent()) {
            loggedInUser.setUser(user.get());
            return "accountDetails?faces-redirect=true";
        } else {
            FacesMessage msg = new FacesMessage(messages.getString("wrongUsernameOrPassword"));
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            context.addMessage("loginForm", msg);
            return "login";
        }
    }

    public String logout() {
        loggedInUser.setUser(null);
        context.getExternalContext().invalidateSession();

        return "login?faces-redirect=true";
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }
}
