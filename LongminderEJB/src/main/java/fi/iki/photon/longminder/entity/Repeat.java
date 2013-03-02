package fi.iki.photon.longminder.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: Repeat
 *
 * Base class for the Repeat hierarchy, stored in a single table
 * in the database (All subclass fields are represented in the table,
 * and only the relevant ones are non-null).
 *
 *
 */
@Entity
@Table(name="REPEAT_HIERARCHY")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="DISCRIMINATOR",
                     discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue("REPEAT")
public class Repeat implements Serializable {

	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

    @Column(nullable=true)
    private int repeatTimes;

    @Column(nullable=true)
	@Temporal(TemporalType.DATE)
    private Date repeatUntil;

    
	public Repeat() {
		super();
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public int getRepeatTimes() {
		return repeatTimes;
	}


	public void setRepeatTimes(int repeatTimes) {
		this.repeatTimes = repeatTimes;
	}


	public Date getRepeatUntil() {
		return repeatUntil;
	}


	public void setRepeatUntil(Date repeatUntil) {
		this.repeatUntil = repeatUntil;
	}
   
}
