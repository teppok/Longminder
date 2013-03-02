package fi.iki.photon.longminder.entity;

import fi.iki.photon.longminder.entity.Repeat;
import java.io.Serializable;
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
   
}
