package fi.iki.photon.longminder.web;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;

import fi.iki.photon.longminder.EmailManager;
import fi.iki.photon.longminder.UserManager;
import fi.iki.photon.longminder.entity.dto.UserDTO;

/**
 * Session Bean implementation class UserManagerService
 */

@Stateless
@LocalBean
public class UserManagerService {

    @EJB
    private UserManager um;

    @EJB
    private EmailManager emm;

    /**
     * Default constructor.
     */
    public UserManagerService() {
    }

    /**
     * Takes the user name and password and calls the HttpServletRequest with
     * these values (properly salted).
     * 
     * @param name
     * @param password
     * @param req
     * @return true if successful
     */

    public boolean login(final String name, final String password,
            final HttpServletRequest req) {
        System.out.println("Login: " + req.getRemoteUser() + " / "
                + req.isUserInRole("USER"));

        if (req.getUserPrincipal() == null) {

            try {
                final UserDTO u = um.find(name);
                if (u == null) {
                    return false;
                }

                req.login(name, DigestUtils.sha512Hex(u.getSalt() + password));
            } catch (final ServletException e) {
                return false;
            }
        }
        return true;
    }

    /**
     * Given an UserDTO instance with valid values, creates a new user and logs
     * him in with login(). Also sends a verification email.
     * 
     * UserDTO is valid if: email, password1, password2, first, last have
     * non-empty strings as values.
     * 
     * @param ud
     * @param req
     * @return true if successful
     */

    public boolean register(final UserDTO ud, final HttpServletRequest req) {

        if (ud == null || ud.getEmail() == null || ud.getPassword1() == null
                || ud.getEmail().length() == 0) {
            return false;
        }
        System.out.println("Registering " + ud.getEmail() + " "
                + ud.getPassword1());

        final UserDTO existingUser = um.find(ud.getEmail());
        if (existingUser != null) {
            return false;
        }

        final List<String> groups = new ArrayList<String>();
        groups.add("USER");
        ud.setGroups(groups);

        if (ud.getLocale() == null) {
            ud.setLocale("en-US");
        }

        System.out.println("Ok this far");

        um.create(ud);

        System.out.println("" + ud.getId() + "-e-" + ud.getEmail());

        final boolean result = login(ud.getEmail(), ud.getPassword1(), req);

        sendVerificationEmail(req);

        return result;
    }

    /**
     * Logs the user out from the Servlet session and invalidates it.
     * 
     * @param req
     * @return true if successful
     */

    public boolean logout(final HttpServletRequest req) {
        if (req.getUserPrincipal() != null) {
            try {
                req.logout();
                req.getSession().invalidate();
            } catch (final ServletException e) {
                // Do nothing in case of failure
            }

        }
        return true;
    }

    /**
     * Given a temporary key, logs the user in using the User data tied to this
     * temporary key.
     * 
     * @param key
     * @param req
     * @return true if successful.
     */

    public boolean loginWithKey(final String key, final HttpServletRequest req) {
        if (req.getUserPrincipal() == null) {
            final UserDTO ud = um.findWithLoginKey(key);

            if (ud == null) {
                return false;
            }

            try {
                req.login(ud.getEmail(), ud.getReturnPassword());
                ud.setReturnPassword(null);
            } catch (final ServletException e) {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Given a temporary key, verifies the email of the user tied to this
     * temporary key.
     * 
     * @param key
     * @return true if successful
     */

    public boolean verify(final String key) {
        return um.verify(key);
    }

    /**
     * Given an UserDTO instance, modifies the user info in the database.
     * UserManager requires that if email or password is changed, old password
     * must be given, and if this fails, we fail.
     * 
     * If the modification was successful and email was modified, we log out the
     * user and log him back in.
     * 
     * @param modifyUser
     * @param req
     * @return true if successful
     */

    public boolean modify(final UserDTO modifyUser, final HttpServletRequest req) {
        final String oldLoginName = req.getRemoteUser();
        if (oldLoginName == null || !oldLoginName.equals(modifyUser.getEmail())) {
            return false;
        }

        final boolean result = um.updateUser(oldLoginName, modifyUser);
        if (result == false) {
            return false;
        }

        if (!oldLoginName.equals(modifyUser.getEmail())) {
            try {
                System.out.println("Logging out!" + modifyUser.getEmail() + " "
                        + modifyUser.getReturnPassword());
                req.logout();
                System.out.println(modifyUser.getReturnPassword());
                req.login(modifyUser.getEmail(), modifyUser.getReturnPassword());
                modifyUser.setReturnPassword(null);
            } catch (final ServletException e) {
                return false;
            }
        }
        return true;
    }

    /**
     * Fills the given UserDTO instance with data from database regarding the
     * current user.
     * 
     * @param req
     * @param modifyUser
     */

    public void fill(final HttpServletRequest req, final UserDTO modifyUser) {
        um.fill(req.getRemoteUser(), modifyUser);

    }

    /**
     * Sends a verification email to the current user.
     * 
     * @param req
     */

    public boolean sendVerificationEmail(final HttpServletRequest req) {
        final String serverBase = req.getScheme() + "://" + req.getServerName()
                + ":" + req.getServerPort() + "/" + req.getContextPath();
        return emm.requestVerificationEmail(serverBase, req.getRemoteUser());
    }

}
