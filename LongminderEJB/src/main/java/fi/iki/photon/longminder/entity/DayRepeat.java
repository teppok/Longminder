package fi.iki.photon.longminder.entity;

import fi.iki.photon.longminder.entity.Repeat;
import java.io.Serializable;
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

	public int getDayDelay() {
		return dayDelay;
	}

	public void setDayDelay(int dayDelay) {
		this.dayDelay = dayDelay;
	}
   
}
