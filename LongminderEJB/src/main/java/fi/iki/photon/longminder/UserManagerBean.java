package fi.iki.photon.longminder;

import java.security.SecureRandom;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.commons.codec.digest.DigestUtils;

import fi.iki.photon.longminder.entity.LoginData;
import fi.iki.photon.longminder.entity.User;
import fi.iki.photon.longminder.entity.dto.UserDTO;

/**
 * Session Bean UserManagerBean for UserManager for managing anything User
 * object related.
 */
@Stateless
@LocalBean
public class UserManagerBean implements UserManager {

    /** Characteds to create random strings. */

    private static final char[] characters = "abcdef0123456789".toCharArray();

    @PersistenceContext
    private EntityManager em;

    /**
     * Default constructor.
     */
    public UserManagerBean() {

    }

    /** Simple ping for testing. */

    @Override
    public String hello() {
        return "Usermanager says Hello!";
    }

    /**
     * Given an UserDTO object, create a new User object based on the values in
     * the object and store it in the Persistence Context.
     * 
     */

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void create(final UserDTO ud) throws LongminderException {
        if (ud == null) {
            throw new LongminderException("Program error.");
        }

        final User utmp = findTrueUserForEmail(ud.getEmail());

        if (utmp != null) {
            throw new LongminderException("User already exists.");
        }

        final User u = new User();
        u.initialize(ud);

        em.persist(u);

        // Flush to database so that the User instance gets its Id value.
        em.flush();

        // Transfer the id value and the returnPassword to the UserDTO object.
        u.initializeDTO(ud);
    }

    /**
     * Given an email, returns an UserDTO instance filled out with values in the
     * corresponding User object.
     */

    @Override
    public UserDTO find(final String email) throws LongminderException {
        final User u = findTrueUserForEmail(email);
        if (u == null) {
            return null;
        }

        final UserDTO ud = new UserDTO();
        u.initializeDTO(ud);
        return ud;
    }

    /** Given an email, returns an User instance with this Email. */

    public User findTrueUserForEmail(final String email) {
        final List<User> users = em
                .createQuery("SELECT u FROM User u WHERE u.email LIKE ?1",
                        User.class).setParameter(1, email).getResultList();

        if (users.size() > 0) {
            return users.get(0);
        }
        return null;
    }

    /** Returns a list of all users whose emails have been verified to work. */

    public List<User> getVerifiedUsers() {
        final TypedQuery<User> result = em.createQuery(
                "SELECT u FROM User u WHERE u.verified = true", User.class);
        return result.getResultList();
    }

    /**
     * Removes an user with the given email.
     * */

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void remove(final String email) throws LongminderException {
        final User u = findTrueUserForEmail(email);

        if (u != null) {
            em.remove(u);
        }
    }

    /** Returns a list of all users. */

    public List<User> findAll() {
        final TypedQuery<User> result = em.createQuery("SELECT u FROM User u",
                User.class);
        return result.getResultList();
    }

    /** Removes all users from the database. */

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void removeAll() {
        for (final User u : findAll()) {
            em.remove(u);
        }
    }

    /**
     * Creates a new temporary login for the User u, adds this LoginData object
     * to the Persistence Context and returns it.
     * 
     * @param u
     * @return LoginData containing the generated temporary key.
     */

    public LoginData createLogin(final User u) {

        final LoginData newLogin = new LoginData();

        boolean doAgain = true;
        String random = null;
        while (doAgain) {
            random = UserManagerBean.generateRandomString(32);
            final LoginData tmp = em.find(LoginData.class, random);
            if (tmp == null) {
                doAgain = false;
            }
        }

        newLogin.setLoginkey(random);
        newLogin.setIssued(new Date());

        u.setLogin(newLogin);

        return newLogin;
    }

    /**
     * Generates a random string using predefined set of characters. Currently
     * represents a hex value, though uses double the amount of random integers
     * than it could.
     * 
     * @param length
     * @return Random salt string of length length
     */

    public static String generateRandomString(final int length) {
        final char[] salt = new char[length];
        final SecureRandom random = new SecureRandom();

        for (int i = 0; i < salt.length; i++) {
            salt[i] = UserManagerBean.characters[random
                    .nextInt(UserManagerBean.characters.length)];
        }

        return new String(salt);

    }

    /**
     * Returns an UserDTO object, whose temporary login key matches the supplied
     * key value.
     */

    @Override
    public UserDTO findWithLoginKey(final String key) throws LongminderException {
        final List<User> users = em
                .createQuery(
                        "SELECT u FROM User u JOIN u.logins l WHERE l.loginkey LIKE ?1",
                        User.class).setParameter(1, key).getResultList();

        if (users.size() > 0) {
            final User u = users.get(0);
            // u.setVerified(true);
            final UserDTO ud = new UserDTO();
            u.initializeDTO(ud);
            return ud;
        }
        return null;
    }

    /**
     * Given a temporary login key, verifies that this user's email is verified.
     */

    @Override
    public boolean verify(final String key) {
        final List<User> users = em
                .createQuery(
                        "SELECT u FROM User u JOIN u.logins l WHERE l.loginkey LIKE ?1",
                        User.class).setParameter(1, key).getResultList();

        if (users.size() > 0) {
            final User u = users.get(0);
            u.setVerified(true);
            return true;
        }
        return false;
    }

    /**
     * Given an user and password, returns true if the User's password matches
     * this.
     * 
     * @param u
     *            User object
     * @param password
     * @return true if the password matches.
     */

    private static boolean verifyPassword(final User u, final String password) {
        if (u == null) {
            return false;
        }

        final String pwHash = DigestUtils.sha512Hex(u.getSalt() + password);

        return pwHash.equals(u.getPassword());

    }

    /**
     * Given an email and an UserDTO object, updates the matching User with the
     * values from the UserDTO (except id). Updating logic is in User.initialize
     * (currently ignores all values set to null).
     * 
     * Allows modification of password only if the supplied oldPassword is the
     * same as the current password.
     * 
     */

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Override
    public void updateUser(final String email, final UserDTO ud) throws LongminderException {
        if (ud == null || email == null) {
            throw new LongminderException("Authentication error.");
        }

        System.out.println(ud.getEmail());

        final User u = findTrueUserForEmail(email);

        if (u == null) {
            throw new LongminderException("Authentication error.");
        }

        // Email change requested
        if (ud.getEmail() != null) {
            if (! email.equals(ud.getEmail())) {
                // Check that the email doesn't already exist with someone else.
                final User tmpUser = findTrueUserForEmail(ud.getEmail());
                if (tmpUser != null) {
                    throw new LongminderException("Email already exists.");
                }
                if (!verifyPassword(u, ud.getOldPassword())) {
                    throw new LongminderException("Password required when changing email.");
                }
            }
        }
        
        System.out.println("Updateuser: " + u.getEmail());

        if (ud.getPassword1() != null) {
            // Password change requested.
            if (!verifyPassword(u, ud.getOldPassword())) {
                throw new LongminderException("Password mismatch.");
            }
        }
        u.initialize(ud);

        if (ud.getEmail() != null) {
            if (! email.equals(ud.getEmail())) {
                u.setVerified(false);
            }
        }
        // Mainly to get returnpassword from u.
        u.initializeDTO(ud);
    }

    /**
     * Given an email and a possibly empty UserDTO object, fills it with data
     * from the database.
     */

    @Override
    public void fill(final String email, final UserDTO ud) throws LongminderException {
        final User u = findTrueUserForEmail(email);

        if (u != null) {
            u.initializeDTO(ud);
        }
    }
    
    @Override
    public void setLocale(final String email, final Locale locale) {
        final User u = findTrueUserForEmail(email);

        if (u != null) {
            u.setLocaleObject(locale);
        }
    }

    @Override
    public Locale getLocale(String email) {
        final User u = findTrueUserForEmail(email);

        if (u != null) {
            return u.getLocaleObject();
        }
        return null;
    }
}
