package fi.iki.photon.longminder.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import fi.iki.photon.longminder.entity.dto.AlertDTO;

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
abstract public class Repeat implements Serializable {

	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

    @Column(nullable=true)
    private Integer repeatTimes;

    @Column(nullable=true)
	@Temporal(TemporalType.DATE)
    private Date repeatUntil;

	public Repeat() {
		super();
	}
	
	/**
	 * Given an AlertDTO object, initializes the Repeat object using the data from the DTO.
	 * @param dto
	 */
	
	public Repeat(AlertDTO dto) {
		setRepeatUntil(dto.getRepeatUntil());
		setRepeatTimes(dto.getRepeatTimes());
	}

	/**
	 * Given an AlertDTO object, transfers the values from this object into it.
	 * @param dto
	 */
	
	public void initializeDTO(AlertDTO dto) {
		dto.setRepeatUntil(getRepeatUntil());
		dto.setRepeatTimes(getRepeatTimes());
	}

	/**
	 * Are there any repeats left, relative to the given date.
	 * @param nextRepeat
	 * @return true if repeats left.
	 */
	
	public boolean isRepeatsLeft(Date nextRepeat) {
		if (repeatTimes == null && repeatUntil == null) return true;
		if (repeatTimes != null && repeatTimes.intValue() == 0) return false;
		if (repeatUntil != null && nextRepeat.after(repeatUntil)) return false;
		return true;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getRepeatTimes() {
		return repeatTimes;
	}

	public void setRepeatTimes(Integer repeatTimes) {
		this.repeatTimes = repeatTimes;
	}

	public Date getRepeatUntil() {
		return repeatUntil;
	}

	public void setRepeatUntil(Date repeatUntil) {
		this.repeatUntil = repeatUntil;
	}

	abstract public Date nextAlert(Date fromDate);
	
}
