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
 * Entity implementation class for Entity: WeekRepeat
 * 
 * Represents a repeat based on n weeks between alerts, and the alert happening
 * on a specified weekday.
 */
@Entity
@Access(AccessType.FIELD)
@DiscriminatorValue("YEARREPEAT")
public class YearRepeat extends Repeat {

    private static final long serialVersionUID = 1L;

    private int yearDelay;

    /**
     * Values 0 to 365
     */

    private int alertYearDay;

    public YearRepeat() {
        super();
    }

    public YearRepeat(final AlertDTO dto) {
        super(dto);

        setYearDelay(dto.getYearDelay());
        setAlertYearDay(dto.getAlertYearDay());
    }

    @Override
    public void initializeDTO(final AlertDTO dto) {
        super.initializeDTO(dto);
        dto.setYearDelay(getYearDelay());
        dto.setAlertYearDay(getAlertYearDay());
        dto.setRepeatType(AlertDTO.REPEAT_YEAR);
    }

    @Override
    public Date nextAlert(final Date fromDate, Locale locale) {
        final Calendar alertCal = Calendar.getInstance(locale);
        alertCal.setTime(fromDate);
        
        alertCal.set(Calendar.DAY_OF_YEAR, 1);

        alertCal.add(Calendar.YEAR, yearDelay);
        alertCal.add(Calendar.DAY_OF_YEAR, alertYearDay - 1);
        return alertCal.getTime();
    }

    public int getYearDelay() {
        return yearDelay;
    }

    public void setYearDelay(int yearDelay) {
        this.yearDelay = yearDelay;
    }

    public int getAlertYearDay() {
        return alertYearDay;
    }

    public void setAlertYearDay(int alertYearDay) {
        this.alertYearDay = alertYearDay;
    }
}
