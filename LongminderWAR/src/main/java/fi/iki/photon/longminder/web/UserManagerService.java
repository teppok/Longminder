package fi.iki.photon.longminder.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;

import fi.iki.photon.longminder.EmailManager;
import fi.iki.photon.longminder.LongminderException;
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
            } catch (final LongminderException e) {
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
     */

    public void register(final UserDTO ud, final HttpServletRequest req) throws LongminderException {

        if (ud == null || ud.getEmail() == null || ud.getPassword1() == null
                || ud.getEmail().length() == 0) {
            throw new LongminderException("Invalid input.");
        }
        System.out.println("Registering " + ud.getEmail() + " "
                + ud.getPassword1());

        final UserDTO existingUser = um.find(ud.getEmail());
        if (existingUser != null) {
            throw new LongminderException("User already exists.");
        }

        final List<String> groups = new ArrayList<String>();
        groups.add("USER");
        ud.setGroups(groups);

        if (ud.getLocale() == null) {
            ud.setLocale(Locale.forLanguageTag("en_US"));
        }

        System.out.println("Ok this far");

        um.create(ud);

        System.out.println("" + ud.getId() + "-e-" + ud.getEmail());

        final boolean result = login(ud.getEmail(), ud.getPassword1(), req);

        sendVerificationEmail(req);

        if (! result) {
            throw new LongminderException("Register/Login failed.");
        }
    }

    /**
     * Logs the user out from the Servlet session and invalidates it.
     * 
     *
     * @param req
     */

    public void logout(final HttpServletRequest req) {
        if (req.getUserPrincipal() != null) {
            try {
                req.logout();
                req.getSession().invalidate();
            } catch (final ServletException e) {
                // Do nothing in case of failure
            }

        }
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
            final UserDTO ud;
            try {
                ud = um.findWithLoginKey(key);
            } catch (LongminderException e) {
                return false;
            }

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

    public boolean verify(final String key) throws LongminderException {
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
     */

    public void modify(final UserDTO modifyUser, final HttpServletRequest req) throws LongminderException {
        final String oldLoginName = req.getRemoteUser();
        if (oldLoginName == null) {
            throw new LongminderException("Authentication error.");
        }

        um.updateUser(oldLoginName, modifyUser);

        if (!oldLoginName.equals(modifyUser.getEmail())) {
            try {
                System.out.println("Logging out!" + modifyUser.getEmail() + " "
                        + modifyUser.getReturnPassword());
                req.logout();
                System.out.println(modifyUser.getReturnPassword());
                req.login(modifyUser.getEmail(), modifyUser.getReturnPassword());
                modifyUser.setReturnPassword(null);
            } catch (final ServletException e) {
                throw new LongminderException("Unknown error.");
            }
        }
    }

    /**
     * Fills the given UserDTO instance with data from database regarding the
     * current user.
     * 
     * @param req
     * @param modifyUser
     */

    public void fill(final HttpServletRequest req, final UserDTO modifyUser) {
        try {
            um.fill(req.getRemoteUser(), modifyUser);
        } catch (LongminderException e) {
            // In case of authentication error raising an exception, do nothing.
            return;
        }
    }

    /**
     * Sends a verification email to the current user.
     * 
     * @param req
     */

    public boolean sendVerificationEmail(final HttpServletRequest req) {
//        final String serverBase = req.getScheme() + "://" + req.getServerName()
//                + ":" + req.getServerPort() + "/" + req.getContextPath();
        return emm.requestVerificationEmail(req.getRemoteUser());
    }

    public boolean isVerified(final HttpServletRequest req) {
        final UserDTO tmpUser = new UserDTO();
        try {
            um.fill(req.getRemoteUser(), tmpUser);
        } catch (LongminderException e) {
            // In case of failure, we probably aren't verified.
            return false;
        }
        return tmpUser.isVerified();
    }

}
