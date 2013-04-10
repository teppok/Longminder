package fi.iki.photon.longminder.entity;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import fi.iki.photon.longminder.entity.dto.AlertDTO;

/**
 * Entity implementation class for Entity: DayRepeat
 * 
 * Represents a repeat based on n days between alerts.
 */
@Entity
@Access(AccessType.FIELD)
@DiscriminatorValue("DAYREPEAT")
public class DayRepeat extends Repeat {

    private static final long serialVersionUID = 1L;

    private int dayDelay;

    public DayRepeat() {
        super();
    }

    /**
     * Initializes the Repeat object based on values in AlertDTO.
     * 
     * @param dto
     */

    public DayRepeat(final AlertDTO dto) {
        super(dto);
        setDayDelay(dto.getDayDelay());

    }

    /**
     * Given an AlertDTO object, inserts the related values into the object.
     * 
     * @param dto
     *            AlertDTO to be initialized.
     * */

    @Override
    public void initializeDTO(final AlertDTO dto) {
        super.initializeDTO(dto);
        dto.setDayDelay(getDayDelay());
        dto.setRepeatType(AlertDTO.REPEAT_DAY);
    }

    /**
     * Gives the next alert calculated from the given date.
     * 
     * @param fromDate
     * @return Date instance representing the new time.
     */

    @Override
    public Date nextAlert(final Date fromDate, Locale locale) {
        final Calendar alertCal = Calendar.getInstance(locale);
        alertCal.setTime(fromDate);
        alertCal.add(Calendar.DAY_OF_MONTH, dayDelay);
        return alertCal.getTime();
    }

    public int getDayDelay() {
        return dayDelay;
    }

    public void setDayDelay(final int dayDelay) {
        this.dayDelay = dayDelay;
    }

}
