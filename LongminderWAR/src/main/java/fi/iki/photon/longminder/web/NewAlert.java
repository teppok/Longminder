package fi.iki.photon.longminder.web;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

/**
 * A backing bean for newalert.xhtml. Actually extends AlertDTO for ease of data
 * transfer.
 * 
 * @author Teppo Kankaanp��
 * 
 */

@ManagedBean
@RequestScoped
public class NewAlert extends WebAlertDTO {

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

        if (!validate()) return null;
        
        if (ams.create(req, this)) {
            return "mainpage";
        }
        final FacesMessage msg = new FacesMessage("Error creating the alert.");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        return null;
    }
}
