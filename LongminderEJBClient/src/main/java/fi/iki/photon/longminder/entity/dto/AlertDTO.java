package fi.iki.photon.longminder.entity.dto;

import java.util.Calendar;
import java.util.Date;

/**
 * Data transfer object to transfer data to and from EJB backend to web
 * application backend. When using as a parameter to enterprise beans, caller
 * must make sure the contents are valid.
 * 
 * 
 * 
 * @author Teppo Kankaanp‰‰
 * 
 */

public class AlertDTO {
    public static final int REPEAT_NO = 0;
    public static final int REPEAT_DAY = 1;
    public static final int REPEAT_WEEK = 2;

    private int id;
    private String description;
    private Date nextAlert;
    private Date repeatUntil;
    private Integer repeatTimes;
    private boolean oneOff;
    private boolean fired;
    private boolean dismissed;

    private int repeatType;

    private int dayDelay;
    private int weekDelay;
    private int alertWeekDay;

    /**
     * A constructor initializing some default values.
     */

    public AlertDTO() {
        oneOff = true;
        dayDelay = 1;
        weekDelay = 1;
    }

    /**
     * How much days left until this alert is due.
     * 
     * @return days left as an int.
     */

    public int getTimeLeft() {
        final Calendar later = Calendar.getInstance();
        later.setTime(nextAlert);
        final Calendar now = Calendar.getInstance();
        final float diffmillis = later.getTimeInMillis()
                - now.getTimeInMillis();
        return (int) Math.ceil(diffmillis / (1000 * 60 * 60 * 24));
    }

    /**
     * Sets oneOff parameter using an integer value. 1 = true, anything else =
     * false.
     * 
     * @param autoRepeat
     */

    public void setOneOffInt(final int autoRepeat) {
        if (autoRepeat == 1) {
            setOneOff(true);
        } else {
            setOneOff(false);
        }
    }

    /**
     * Returns the oneOff parameter as an integer.
     * 
     * @return 1 = true, 2 = false.
     */

    public int getOneOffInt() {
        if (isOneOff()) {
            return 1;
        }
        return 2;
    }

    public boolean isPast() {
        return (nextAlert.before(new Date()));
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Date getRepeatUntil() {
        return repeatUntil == null ? null : (Date) repeatUntil.clone();
    }

    public void setRepeatUntil(final Date repeatUntil) {
        this.repeatUntil = (repeatUntil == null ? null : (Date) repeatUntil
                .clone());
    }

    public Integer getRepeatTimes() {
        return repeatTimes;
    }

    public void setRepeatTimes(final Integer repeatTimes) {
        this.repeatTimes = repeatTimes;
    }

    public Date getNextAlert() {
        return nextAlert == null ? null : (Date) nextAlert.clone();
    }

    public void setNextAlert(final Date nextAlert) {
        this.nextAlert = (nextAlert == null ? null : (Date) nextAlert.clone());
    }

    public int getDayDelay() {
        return dayDelay;
    }

    public void setDayDelay(final int dayDelay) {
        this.dayDelay = dayDelay;
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

    public int getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(final int repeatType) {
        this.repeatType = repeatType;
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public boolean isOneOff() {
        return oneOff;
    }

    public void setOneOff(final boolean oneOff) {
        this.oneOff = oneOff;
    }

    public boolean isDismissed() {
        return dismissed;
    }

    public void setDismissed(final boolean dismissed) {
        this.dismissed = dismissed;
    }

    public boolean isFired() {
        return fired;
    }

    public void setFired(final boolean fired) {
        this.fired = fired;
    }
}
