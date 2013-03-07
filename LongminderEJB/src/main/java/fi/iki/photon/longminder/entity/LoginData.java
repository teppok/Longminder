package fi.iki.photon.longminder.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Entity implementation class for Entity LoginData
 * 
 * Contains a generated temporary loginkey for the user (field OWNER generated
 * by definitions in User.java).
 * 
 */
@Entity
public class LoginData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String loginkey;

    @Temporal(TemporalType.TIMESTAMP)
    private Date issued;

    public LoginData() {
        super();
    }

    public String getLoginkey() {
        return loginkey;
    }

    public void setLoginkey(final String loginkey) {
        this.loginkey = loginkey;
    }

    public Date getIssued() {
        return issued == null ? null : (Date) issued.clone();
    }

    public void setIssued(final Date issued) {
        this.issued = (issued == null ? null : (Date) issued.clone());
    }

}
