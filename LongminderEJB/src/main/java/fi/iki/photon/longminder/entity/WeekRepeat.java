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
@DiscriminatorValue("WEEKREPEAT")
public class WeekRepeat extends Repeat {

    private static final long serialVersionUID = 1L;

    private int weekDelay;

    /**
     * Values 0 to 6 (Monday to Sunday)
     */

    private int alertWeekDay;

    public WeekRepeat() {
        super();
    }

    public WeekRepeat(final AlertDTO dto) {
        super(dto);

        setWeekDelay(dto.getWeekDelay());
        setAlertWeekDay(dto.getAlertWeekDay());
    }

    @Override
    public void initializeDTO(final AlertDTO dto) {
        super.initializeDTO(dto);
        dto.setWeekDelay(getWeekDelay());
        dto.setAlertWeekDay(getAlertWeekDay());
        dto.setRepeatType(AlertDTO.REPEAT_WEEK);
    }

    public int getWeekDelay() {
        return weekDelay;
    }

    public void setWeekDelay(final int weekDelay) {
        this.weekDelay = weekDelay;
    }

    public int getAlertWeekDay() {
        return alertWeekDay;
    }

    public void setAlertWeekDay(final int alertWeekDay) {
        this.alertWeekDay = alertWeekDay;
    }

    @Override
    public Date nextAlert(final Date fromDate, Locale locale) {
        final Calendar alertCal = Calendar.getInstance(locale);
        alertCal.setTime(fromDate);
        
//        alertCal.set(Calendar.DAY_OF_WEEK, alertCal.getFirstDayOfWeek());

        int dayOfWeek = (alertCal.get(Calendar.DAY_OF_WEEK) + 7 - alertCal.getFirstDayOfWeek()) % 7;
        alertCal.add(Calendar.DAY_OF_YEAR, -dayOfWeek);
        
        alertCal.add(Calendar.WEEK_OF_YEAR, weekDelay);
        alertCal.add(Calendar.DAY_OF_MONTH, alertWeekDay);
        return alertCal.getTime();
    }
}
