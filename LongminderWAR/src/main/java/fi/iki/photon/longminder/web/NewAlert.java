package fi.iki.photon.longminder.web;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import fi.iki.photon.longminder.entity.dto.AlertDTO;
import fi.iki.photon.longminder.entity.dto.UserDTO;

/**
 * A backing bean for newalert.xhtml. Actually extends AlertDTO for ease of data transfer.
 * 
 * @author Teppo Kankaanp‰‰
 *
 */

@ManagedBean
@RequestScoped
public class NewAlert extends AlertDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	@EJB AlertManagerService ams;

	/**
	 * Requests that the alert should be created.
	 * @return "mainpage" on success, null on error.
	 */
	
	public String create() {
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest req = (HttpServletRequest) context.getExternalContext()
							.getRequest();

		// TODO Perform more verification.
		
		if ("".equals(getDescription())) {
			FacesMessage msg = new FacesMessage("Error creating the alert: Set the description.");
			FacesContext.getCurrentInstance().addMessage(null, msg);
			return null;
		}

		if (getRepeatType() == AlertDTO.REPEAT_DAY && getDayDelay() < 1) {
			FacesMessage msg = new FacesMessage("Error creating the alert: Day delay value must be a positive integer.");
			FacesContext.getCurrentInstance().addMessage(null, msg);
			return null;
		}

		if (getRepeatType() == AlertDTO.REPEAT_WEEK && getWeekDelay() < 1) {
			FacesMessage msg = new FacesMessage("Error creating the alert: Week delay value must be a positive integer.");
			FacesContext.getCurrentInstance().addMessage(null, msg);
			return null;
		}

		if (ams.create(req, this)) return "mainpage";
		FacesMessage msg = new FacesMessage("Error creating the alert.");
		FacesContext.getCurrentInstance().addMessage(null, msg);
		return null;
	}
}
