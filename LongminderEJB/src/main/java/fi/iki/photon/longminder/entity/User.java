package fi.iki.photon.longminder.entity;

import java.io.Serializable;
import javax.persistence.*;

import org.apache.commons.codec.digest.DigestUtils;

import fi.iki.photon.longminder.UserManagerBean;
import fi.iki.photon.longminder.entity.Group;
import fi.iki.photon.longminder.entity.dto.UserDTO;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;


/**
 * The persistent class for the USERS database table.
 * 
 * 
 */
@Entity
@Table(name="USERS")
@Cacheable(false)
public class User extends fi.iki.photon.utils.Entity implements Serializable {
	
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

	@Column(nullable=false, length=128)
	private String email;

	@Column(nullable=false)
	private boolean verified;
	
	@Column(nullable=false, length=128)
	private String firstname;

	@Column(nullable=false, length=128)
	private String lastname;

	@Column(nullable=false, length=128)
	private String password;

	@Column(nullable=false, length=128)
	private String salt;

	@Column(nullable=false, length=32)
	private String locale;
	
	//bi-directional one-to-many association to Alert
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name="OWNER")
	private List<Alert> alerts;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name="OWNER")
	private List<LoginData> logins;

	// The time to send a new email, if there are no new alerts.
	@Temporal(TemporalType.DATE)
    @Column(nullable=false)
	private Date nextEmail;

	/** Collection of Group enum objects will be automatically mapped by JPA to USER_IN_GROUP table
	 *  nicely.
	 */
	
	@ElementCollection(targetClass = Group.class)
    @CollectionTable(name = "USER_IN_GROUP",
                    joinColumns       = @JoinColumn(name = "email", referencedColumnName="email", nullable=false),
                    uniqueConstraints = { @UniqueConstraint(columnNames={"email","groupname"}) } )
    @Enumerated(EnumType.STRING)
    @Column(name="groupname", length=64, nullable=false)
    private List<Group> groups;
    
	public User() {
		nextEmail = new Date();
	}

	/** Given an UserDTO, updates the fields in this User object.
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
	
	public void initialize(UserDTO ud) {

		// First time when initializing a new User, password must be non-null and of proper length.
		if (this.password == null) {
			if (ud.getPassword1() == null || ud.getPassword1().length() == 0) {
				throw new RuntimeException("Invalid passwords");
			}
		}
		if (ud.getPassword1() != null && ! (ud.getPassword1().equals(ud.getPassword2()) ) ) {
			throw new RuntimeException("Invalid passwords");
		}
		
		if (this.email == null) {
			if (ud.getEmail() == null || ud.getEmail().length() == 0) {
				throw new RuntimeException("Invalid email");
			}
		}

		if (ud.getFirst() != null) { this.firstname = ud.getFirst(); }
		if (ud.getLast() != null) { this.lastname = ud.getLast(); }

		if (this.email != null && this.email.equals(ud.getEmail())) {
			// Don't modify email verified value if the email is the same as before.
		} else {
			this.verified = false;
		}
		if (ud.getEmail() != null) { this.email = ud.getEmail(); }

		if (ud.getPassword1() != null) {
			this.setSalt(generateSalt());
			this.password = DigestUtils.sha512Hex(this.salt + ud.getPassword1());
		}

		if (ud.getGroups() != null) {
			this.groups = new ArrayList<Group>(ud.getGroups().size());
			for (String g : ud.getGroups()) {
				this.groups.add(Group.valueOf(g));
			}
		}

		if (ud.getLocale() != null) { this.setLocale(ud.getLocale()); }
	}

	/** Given a UserDTO, initialize its values using the values from this User object. 
	 * 
	 * @param ud
	 * */
	
	public void initializeDTO(UserDTO ud) {
		if (ud == null) return;
		ud.setId(getId());
		ud.setFirst(getFirstname());
		ud.setLast(getLastname());
		ud.setEmail(getEmail());
		ud.setSalt(getSalt());
		ud.setVerified(isVerified());
		ud.setLocale(getLocale());
		
		List<String> stringGroups = new ArrayList<String>(getGroups().size());
		
		for (Group g : getGroups()) {
			stringGroups.add(g.name());
		}
		ud.setGroups(stringGroups);

		ud.setReturnPassword(getPassword());
	}

	/**
	 * Generates a salt string.
	 * @return
	 */
	
	private static String generateSalt() {
		return UserManagerBean.generateRandomString(128);
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstname() {
		return this.firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return this.lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public List<Alert> getAlerts() {
		return alerts;
	}

	public void setAlerts(List<Alert> alerts) {
		this.alerts = alerts;
	}

	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	public List<LoginData> getLogins() {
		return logins;
	}

	public void setLogins(List<LoginData> logins) {
		this.logins = logins;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public Date getNextEmail() {
		return nextEmail;
	}

	public void setNextEmail(Date nextEmail) {
		this.nextEmail = nextEmail;
	}
}
