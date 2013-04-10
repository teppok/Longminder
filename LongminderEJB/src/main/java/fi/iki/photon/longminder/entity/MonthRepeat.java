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
@DiscriminatorValue("MONTHREPEAT")
public class MonthRepeat extends Repeat {

    private static final long serialVersionUID = 1L;

    private int monthDelay;

    /**
     * Values 0 to 31
     */

    private int alertMonthDay;

    public MonthRepeat() {
        super();
    }

    public MonthRepeat(final AlertDTO dto) {
        super(dto);

        setMonthDelay(dto.getMonthDelay());
        setAlertMonthDay(dto.getAlertMonthDay());
    }

    @Override
    public void initializeDTO(final AlertDTO dto) {
        super.initializeDTO(dto);
        dto.setMonthDelay(getMonthDelay());
        dto.setAlertMonthDay(getAlertMonthDay());
        dto.setRepeatType(AlertDTO.REPEAT_MONTH);
    }

    @Override
    public Date nextAlert(final Date fromDate, Locale locale) {
        final Calendar alertCal = Calendar.getInstance(locale);
        alertCal.setTime(fromDate);
        
        alertCal.set(Calendar.DAY_OF_MONTH, 1);

        alertCal.add(Calendar.MONTH, monthDelay);
        alertCal.add(Calendar.DAY_OF_MONTH, alertMonthDay - 1);
        return alertCal.getTime();
    }

    public int getAlertMonthDay() {
        return alertMonthDay;
    }

    public void setAlertMonthDay(int alertMonthDay) {
        this.alertMonthDay = alertMonthDay;
    }

    public int getMonthDelay() {
        return monthDelay;
    }

    public void setMonthDelay(int monthDelay) {
        this.monthDelay = monthDelay;
    }
}
