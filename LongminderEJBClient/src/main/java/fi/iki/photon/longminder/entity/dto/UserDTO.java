package fi.iki.photon.longminder.entity.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Data transfer object for transferring data between web and EJB backends.
 * 
 * Caller must make sure the data is valid.
 * 
 * Empty strings for password1, password2, oldPassword, first, last, email are
 * not allowed - they are converted to null pointers.
 * 
 * @author Teppo Kankaanp‰‰
 * 
 */

public class UserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String password1;
    private String password2;
    private String oldPassword;
    private String returnPassword;
    private String first;
    private String last;
    private String email;
    private String salt;
    private String locale;

    private boolean verified;

    private List<String> groups;

    /**
     * Default constructor.
     */

    public UserDTO() {
        // Default empty constructor.
    }

    public String getPassword1() {
        return password1;
    }

    public void setPassword1(String password1) {
        this.password1 = password1;
        if (password1 != null && password1.length() == 0) {
            this.password1 = null;
        }
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
        if (password2 != null && password2.length() == 0) {
            this.password2 = null;
        }
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
        if (first != null && first.length() == 0) {
            this.first = null;
        }
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
        if (last != null && last.length() == 0) {
            this.last = null;
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        if (email != null && email.length() == 0) {
            this.email = null;
        }
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public String getReturnPassword() {
        return returnPassword;
    }

    public void setReturnPassword(String returnPassword) {
        this.returnPassword = returnPassword;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
        if (oldPassword != null && oldPassword.length() == 0) {
            this.oldPassword = null;
        }
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
}
