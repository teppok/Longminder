package fi.iki.photon.longminder.entity;

import fi.iki.photon.longminder.entity.Repeat;
import fi.iki.photon.longminder.entity.dto.AlertDTO;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: WeekRepeat
 *
 * Represents a repeat based on n weeks between alerts, and the
 * alert happening on a specified weekday.
 */
@Entity
@DiscriminatorValue("WEEKREPEAT")
public class WeekRepeat extends Repeat implements Serializable {

	
	private static final long serialVersionUID = 1L;

	private int weekDelay;

	/**
	 * Values 0 to 6 (Monday to Sunday)
	 */
	
	private int alertWeekDay;
	
	public WeekRepeat() {
		super();
	}

	public WeekRepeat(AlertDTO dto) {
		super(dto);
		
		setWeekDelay(dto.getWeekDelay());
		setAlertWeekDay(dto.getAlertWeekDay());
	}

	public void initializeDTO(AlertDTO dto) {
		super.initializeDTO(dto);
		dto.setWeekDelay(getWeekDelay());
		dto.setAlertWeekDay(getAlertWeekDay());
		dto.setRepeatType(AlertDTO.REPEAT_WEEK);
	}

	public int getWeekDelay() {
		return weekDelay;
	}


	public void setWeekDelay(int weekDelay) {
		this.weekDelay = weekDelay;
	}


	public int getAlertWeekDay() {
		return alertWeekDay;
	}


	public void setAlertWeekDay(int alertWeekDay) {
		this.alertWeekDay = alertWeekDay;
	}

	// TODO Internationalize date calculation.
	
	public Date nextAlert(Date fromDate) {
		Calendar alertCal = Calendar.getInstance();
		alertCal.setTime(fromDate);
		// Returns 1 = sunday, 7 = saturday
		int dayOfWeek = alertCal.get(Calendar.DAY_OF_WEEK);
		if (dayOfWeek == 1) dayOfWeek = 8;
		dayOfWeek = dayOfWeek - 2;
		// Now monday = 0, sunday = 6
		alertCal.add(Calendar.DAY_OF_MONTH, (7 - dayOfWeek));
		alertCal.add(Calendar.WEEK_OF_YEAR, weekDelay - 1);
		alertCal.add(Calendar.DAY_OF_MONTH, alertWeekDay);
		return alertCal.getTime();
	}
}
