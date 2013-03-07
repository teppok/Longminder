package fi.iki.photon.longminder.entity;

import fi.iki.photon.longminder.entity.Repeat;
import fi.iki.photon.longminder.entity.dto.AlertDTO;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: DayRepeat
 * 
 * Represents a repeat based on n days between alerts.
 */
@Entity
@DiscriminatorValue("DAYREPEAT")
public class DayRepeat extends Repeat implements Serializable {

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

    public DayRepeat(AlertDTO dto) {
        super(dto);
        setDayDelay(dto.getDayDelay());

    }

    /**
     * Given an AlertDTO object, inserts the related values into the object.
     * 
     * @param dto
     *            AlertDTO to be initialized.
     * */

    public void initializeDTO(AlertDTO dto) {
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

    public Date nextAlert(Date fromDate) {
        Calendar alertCal = Calendar.getInstance();
        alertCal.setTime(fromDate);
        alertCal.add(Calendar.DAY_OF_MONTH, dayDelay);
        return alertCal.getTime();
    }

    public int getDayDelay() {
        return dayDelay;
    }

    public void setDayDelay(int dayDelay) {
        this.dayDelay = dayDelay;
    }

}
