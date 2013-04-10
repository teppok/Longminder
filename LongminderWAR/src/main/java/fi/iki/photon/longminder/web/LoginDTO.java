package fi.iki.photon.longminder.web;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.servlet.http.HttpServletRequest;

import fi.iki.photon.longminder.LongminderException;

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
    private Boolean verified;
    private boolean authorized;

    @ManagedProperty(value="#{language}")
    private Language lang;
    
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
        System.out.println(getLang());
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
            try {
                setVerified(Boolean.valueOf(ums.verify(key)));
            } catch (LongminderException e) {
                // Ignore exceptions.
            }
        }
    }

    /**
     * Takes the email and password input and calls UserManagerService to log in
     * the user.
     * 
     * @return "mainpage" if successful, null otherwise and sets a Faces error.
     */

    public synchronized String login() {

        final HttpServletRequest req = (HttpServletRequest) FacesContext
                .getCurrentInstance().getExternalContext().getRequest();

        final boolean r = ums.login(email, password, req);

        if (!r) {
            final FacesMessage msg = new FacesMessage("Login failed");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        }
        
        getLang().setLocale(null);

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
        UserManagerService.logout(req);
        
        getLang().setLocale(null);
        
        return "login";
    }


    public synchronized String getEmail() {
        return email;
    }

    public synchronized void setEmail(final String email) {
        this.email = email;
    }

    public synchronized String getPassword() {
        return password;
    }

    public synchronized void setPassword(final String password) {
        this.password = password;
    }

    public boolean isVerified() {
        if (getVerified() == null) {
            final FacesContext context = FacesContext.getCurrentInstance();
            final HttpServletRequest req = (HttpServletRequest) context
                    .getExternalContext().getRequest();
            setVerified(Boolean.valueOf(ums.isVerified(req)));
        }
        System.out.println("Isverified " + getVerified());
        return getVerified().booleanValue();
    }

    public synchronized void setVerified(final boolean verified) {
        this.verified = Boolean.valueOf(verified);
    }

    public synchronized boolean isAuthorized() {
        return authorized;
    }

    public synchronized void setAuthorized(final boolean authorized) {
        this.authorized = authorized;
    }

    public synchronized Language getLang() {
        return lang;
    }

    public synchronized void setLang(Language lang) {
        this.lang = lang;
    }

    public synchronized Boolean getVerified() {
        return verified;
    }

    public synchronized void setVerified(Boolean verified) {
        this.verified = verified;
    }

}
