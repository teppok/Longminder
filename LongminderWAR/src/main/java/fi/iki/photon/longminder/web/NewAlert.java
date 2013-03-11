package fi.iki.photon.longminder.web;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import fi.iki.photon.longminder.entity.dto.AlertDTO;

/**
 * A backing bean for newalert.xhtml. Actually extends AlertDTO for ease of data
 * transfer.
 * 
 * @author Teppo Kankaanp��
 * 
 */

@ManagedBean
@RequestScoped
public class NewAlert extends AlertDTO {

    @EJB
    private AlertManagerService ams;

    /**
     * Requests that the alert should be created.
     * 
     * @return "mainpage" on success, null on error.
     */

    public String create() {
        final FacesContext context = FacesContext.getCurrentInstance();
        final HttpServletRequest req = (HttpServletRequest) context
                .getExternalContext().getRequest();

        // TODO Perform more verification.

        if ("".equals(getDescription())) {
            final FacesMessage msg = new FacesMessage(
                    "Error creating the alert: Set the description.");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        }

        if (getRepeatType() == AlertDTO.REPEAT_DAY && getDayDelay() < 1) {
            final FacesMessage msg = new FacesMessage(
                    "Error creating the alert: Day delay value must be a positive integer.");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        }

        if (getRepeatType() == AlertDTO.REPEAT_WEEK && getWeekDelay() < 1) {
            final FacesMessage msg = new FacesMessage(
                    "Error creating the alert: Week delay value must be a positive integer.");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        }

        if (ams.create(req, this)) {
            return "mainpage";
        }
        final FacesMessage msg = new FacesMessage("Error creating the alert.");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        return null;
    }
}
