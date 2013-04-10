package fi.iki.photon.longminder.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.apache.commons.codec.digest.DigestUtils;

import fi.iki.photon.longminder.LongminderException;
import fi.iki.photon.longminder.UserManagerBean;
import fi.iki.photon.longminder.entity.dto.UserDTO;

/**
 * The persistent class for the USERS database table.
 * 
 * 
 */
@Entity
@Table(name = "USERS")
@Access(AccessType.FIELD)
@Cacheable(false)
public class User extends fi.iki.photon.utils.Entity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 128)
    private String email;

    @Column(nullable = false)
    private boolean verified;

    @Column(nullable = false, length = 128)
    private String firstname;

    @Column(nullable = false, length = 128)
    private String lastname;

    @Column(nullable = false, length = 128)
    private String password;

    @Column(nullable = false, length = 128)
    private String salt;

    @Column(nullable = false, length = 32)
    private String locale;

    // bi-directional one-to-many association to Alert
    @OneToMany(cascade = CascadeType.ALL, targetEntity = Alert.class)
    @JoinColumn(name = "OWNER")
    private List<Alert> alerts;

    @OneToMany(cascade = CascadeType.ALL, targetEntity = LoginData.class)
    @JoinColumn(name = "OWNER")
    private List<LoginData> logins;

    // The time to send a new email, if there are no new alerts.
    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date nextEmail;

    /**
     * Collection of Group enum objects will be automatically mapped by JPA to
     * USER_IN_GROUP table nicely.
     */

    @ElementCollection(targetClass = Group.class)
    @CollectionTable(name = "USER_IN_GROUP", joinColumns = @JoinColumn(name = "email", referencedColumnName = "email", nullable = false), uniqueConstraints = { @UniqueConstraint(columnNames = {
            "email", "groupname" }) })
    @Enumerated(EnumType.STRING)
    @Column(name = "groupname", length = 64, nullable = false)
    private List<Group> groups;

    public User() {
        nextEmail = new Date();
    }

    /**
     * Given an UserDTO, updates the fields in this User object.
     * 
     * If the User object is uninitialized, password must be supplied.
     * 
     * If any fields in UserDTO are null, they are ignored.
     * 
     * If the email is changed, change the verified status to false.
     * 
     * If the password is changed, generate a new salt.
     * 
     * @param ud
     */

    public void initialize(final UserDTO ud) throws LongminderException {

        // First time when initializing a new User, password must be non-null
        // and of proper length.
        if (password == null) {
            if (ud.getPassword1() == null || ud.getPassword1().length() == 0) {
                throw new LongminderException("Invalid passwords.");
            }
        }
        if (ud.getPassword1() != null
                && !(ud.getPassword1().equals(ud.getPassword2()))) {
            throw new LongminderException("Password mismatch.");
        }

        if (email == null) {
            if (ud.getEmail() == null || ud.getEmail().length() == 0) {
                throw new LongminderException("Invalid email.");
            }
        }

        if (ud.getFirst() != null) {
            firstname = ud.getFirst();
        }
        if (ud.getLast() != null) {
            lastname = ud.getLast();
        }

        if (ud.getEmail() != null) {
            email = ud.getEmail();
        }

        if (ud.getPassword1() != null) {
            setSalt(User.generateSalt());
            password = DigestUtils.sha512Hex(salt + ud.getPassword1());
        }

        if (ud.getGroups() != null) {
            getGroups().clear();
            for (final String g : ud.getGroups()) {
                addGroup(Group.valueOf(g));
            }
        }

        if (ud.getLocale() != null) {
            setLocale(ud.getLocale().toLanguageTag());
        }
    }

    /**
     * Given a UserDTO, initialize its values using the values from this User
     * object.
     * 
     * @param ud
     * */

    public void initializeDTO(final UserDTO ud) {
        if (ud == null) {
            return;
        }
        ud.setId(getId());
        ud.setFirst(getFirstname());
        ud.setLast(getLastname());
        ud.setEmail(getEmail());
        ud.setSalt(getSalt());
        ud.setVerified(isVerified());
        ud.setLocale(getLocaleObject());

        final List<String> stringGroups = new ArrayList<>(getGroups()
                .size());

        for (final Group g : getGroups()) {
            stringGroups.add(g.name());
        }
        ud.setGroups(stringGroups);

        ud.setReturnPassword(getPassword());
    }

    /**
     * Generates a salt string.
     * 
     * @return
     */

    private static String generateSalt() {
        return UserManagerBean.generateRandomString(128);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(final String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(final String lastname) {
        this.lastname = lastname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public void addGroup(Group g) {
        if (groups == null) groups = new ArrayList<>();
        groups.add(g);
    }
    
    private List<Group> getGroups() {
        return groups;
    }
    
    public String getSalt() {
        return salt;
    }

    public void setSalt(final String salt) {
        this.salt = salt;
    }

    public void addAlert(Alert a) {
        if (alerts == null) alerts = new ArrayList<>();
        alerts.add(a);
    }

    public void addAllAlerts(List<Alert> a) {
        if (alerts == null) alerts = new ArrayList<>();
        alerts.addAll(a);
    }

    
    public boolean isVerified() {
        return verified;
    }

    public void setVerified(final boolean verified) {
        this.verified = verified;
    }
    
    public LoginData getLogin() {
        if (logins == null || logins.size() == 0) return null;
        return logins.get(0);
    }
    
    public void setLogin(LoginData login) {
        if (logins == null) logins = new ArrayList<>();
        logins.clear();
        logins.add(login);
    }
    
    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getLocale() {
        return locale;
    }

    public Locale getLocaleObject() {
        return Locale.forLanguageTag(locale);
    }

    public void setLocaleObject(Locale locale) {
        this.locale = locale.toLanguageTag();
    }
    
    public void setLocale(final String locale) {
        this.locale = locale;
    }

    public Date getNextEmail() {
        return nextEmail == null ? null : (Date) nextEmail.clone();
    }

    public void setNextEmail(final Date nextEmail) {
        this.nextEmail = (nextEmail == null ? null : (Date) nextEmail.clone());
    }
}
