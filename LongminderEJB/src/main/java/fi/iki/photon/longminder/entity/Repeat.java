package fi.iki.photon.longminder.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import fi.iki.photon.longminder.entity.dto.AlertDTO;

/**
 * Entity implementation class for Entity: Repeat
 * 
 * Base class for the Repeat hierarchy, stored in a single table in the database
 * (All subclass fields are represented in the table, and only the relevant ones
 * are non-null).
 * 
 * 
 */
@Entity
@Access(AccessType.FIELD)
@Table(name = "REPEAT_HIERARCHY")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "DISCRIMINATOR", discriminatorType = DiscriminatorType.STRING)
abstract public class Repeat implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = true)
    private Integer repeatTimes;

    @Column(nullable = true)
    @Temporal(TemporalType.DATE)
    private Date repeatUntil;

    public Repeat() {
        super();
    }

    /**
     * Given an AlertDTO object, initializes the Repeat object using the data
     * from the DTO.
     * 
     * @param dto
     */

    public Repeat(final AlertDTO dto) {
        setRepeatUntil(dto.getRepeatUntil());
        setRepeatTimes(dto.getRepeatTimes());
    }

    /**
     * Given an AlertDTO object, transfers the values from this object into it.
     * 
     * @param dto
     */

    public void initializeDTO(final AlertDTO dto) {
        dto.setRepeatUntil(getRepeatUntil());
        dto.setRepeatTimes(getRepeatTimes());
    }

    /**
     * Are there any repeats left, relative to the given date.
     * 
     * @param nextRepeat
     * @return true if repeats left.
     */

    public boolean isRepeatsLeft(final Date nextRepeat) {
        if (repeatTimes == null && repeatUntil == null) {
            return true;
        }
        if (repeatTimes != null && repeatTimes.intValue() == 0) {
            return false;
        }
        return !(repeatUntil != null && nextRepeat.after(repeatUntil));
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public Integer getRepeatTimes() {
        return repeatTimes;
    }

    public void setRepeatTimes(final Integer repeatTimes) {
        this.repeatTimes = repeatTimes;
    }

    public Date getRepeatUntil() {
        return repeatUntil == null ? null : (Date) repeatUntil.clone();
    }

    public void setRepeatUntil(final Date repeatUntil) {
        this.repeatUntil = (repeatUntil == null ? null : (Date) repeatUntil
                .clone());
    }

    abstract public Date nextAlert(Date fromDate, Locale locale);

}
