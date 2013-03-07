package fi.iki.photon.longminder.entity;

import fi.iki.photon.longminder.entity.User;
import java.io.Serializable;
import java.lang.String;
import java.util.Date;
import javax.persistence.*;

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
        return this.loginkey;
    }

    public void setLoginkey(String loginkey) {
        this.loginkey = loginkey;
    }

    public Date getIssued() {
        return this.issued == null ? null : (Date) issued.clone();
    }

    public void setIssued(Date issued) {
        this.issued = (issued == null ? null : (Date) issued.clone());
    }

}
