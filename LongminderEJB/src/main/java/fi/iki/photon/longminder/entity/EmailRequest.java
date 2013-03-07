package fi.iki.photon.longminder.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * Entity implementation class for Entity: EmailRequest
 * 
 */
@Entity
public class EmailRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @OneToOne
    @JoinColumn(nullable = false)
    private User owner;

    public EmailRequest() {
        super();
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

}
