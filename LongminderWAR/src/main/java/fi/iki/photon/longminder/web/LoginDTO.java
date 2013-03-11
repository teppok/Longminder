package fi.iki.photon.longminder.web;

import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.servlet.http.HttpServletRequest;

/**
 * A backing bean storing login information for the user.
 * 
 * When not logged in, email and password will be set by login prompt to send
 * data from form to backing bean.
 * 
 * When logged in, email, verified, authorized will be set by the backing bean
 * for every page to transfer login information to JSF pages.
 * 
 * @author Teppo Kankaanp‰‰
 * 
 */

@ManagedBean
@RequestScoped
public class LoginDTO {
    @EJB
    private UserManagerService ums;

    private String email;
    private String password;
    private boolean verified;
    private boolean authorized;

    /**
     * Checks the HttpServletRequest. If it contains a temporary login key,
     * calls UserManagerService to log in with that key. If Servlet login
     * authentication data is present, stores the username to email variable and
     * sets authorized to true. Otherwise sets email to null and authorized to
     * false.
     */

    @PostConstruct
    public void initLogin() {
        System.out.println("Init login!");
        final HttpServletRequest req = (HttpServletRequest) FacesContext
                .getCurrentInstance().getExternalContext().getRequest();

        /*
         * DISABLED LOGIN WITH TEMPORARY KEY UNTIL FURTHER
         * REASSESSMENT 11.3.2013
         * 
        final String key = req.getParameter("key");
        if (key != null) {
            System.out.println("Login with key: " + key);
            if (!ums.loginWithKey(key, req)) {
                setEmail(null);
                setAuthorized(false);
                return;
            }
        }
        */

        System.out.println(req.getRemoteUser());
        if (req.getUserPrincipal() != null) {
            setAuthorized(true);
            setEmail(req.getRemoteUser());
            return;
        }
        setEmail(null);
        setAuthorized(false);
    }

    /**
     * Performed on login page on preRenderView. If user is already authorized,
     * sends him to mainpage instead.
     * 
     * @param cse
     */
    public void checkLoginOnLoginPage(final ComponentSystemEvent cse) {
        final FacesContext context = FacesContext.getCurrentInstance();
        if (isAuthorized()) {
            context.getApplication().getNavigationHandler()
                    .handleNavigation(context, null, "mainpage");
        }
    }

    /**
     * Performed on inner pages on preRenderView. If user is not authorized,
     * sends him to login page instead.
     * 
     * @param cse
     */
    public void checkLogin(final ComponentSystemEvent cse) {
        System.out.println("CHECK LOGIN");
        final FacesContext context = FacesContext.getCurrentInstance();
        if (!isAuthorized()) {
            System.out.println("NO LOGIN");
            context.getApplication().getNavigationHandler()
                    .handleNavigation(context, null, "login");
        }
    }

    /**
     * Performed on verify.xhtml on preRenderView. If the key parameter is set,
     * then use UserManagerService to perform email verification for this key.
     * 
     * @param cse
     */
    public void verify(final ComponentSystemEvent cse) {
        final FacesContext context = FacesContext.getCurrentInstance();
        final HttpServletRequest req = (HttpServletRequest) context
                .getExternalContext().getRequest();

        final String key = req.getParameter("key");
        if (key != null) {
            verified = ums.verify(key);
        }
    }

    /**
     * Takes the email and password input and calls UserManagerService to log in
     * the user.
     * 
     * @return "mainpage" if successful, null otherwise and sets a Faces error.
     */

    public String login() {

        final HttpServletRequest req = (HttpServletRequest) FacesContext
                .getCurrentInstance().getExternalContext().getRequest();

        final boolean r = ums.login(email, password, req);

        if (!r) {
            final FacesMessage msg = new FacesMessage("Login failed");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        }

        return "mainpage";
    }

    /**
     * Uses UserManagerService to log the user out.
     * 
     * @return "login"
     */

    public String logout() {
        final FacesContext context = FacesContext.getCurrentInstance();
        final HttpServletRequest req = (HttpServletRequest) context
                .getExternalContext().getRequest();
        ums.logout(req);
        return "login";
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(final boolean verified) {
        this.verified = verified;
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public void setAuthorized(final boolean authorized) {
        this.authorized = authorized;
    }

}
