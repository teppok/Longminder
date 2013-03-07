package fi.iki.photon.longminder.web;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.servlet.http.HttpServletRequest;

import fi.iki.photon.longminder.entity.dto.AlertDTO;

/**
 * A backing bean for modifyalert.xhtml. Extends AlertDTO so the values are easy
 * to set and read.
 * 
 * 
 * @author Teppo Kankaanp‰‰
 * 
 */

@ManagedBean
@RequestScoped
public class ModifyAlert extends AlertDTO {

    @EJB
    AlertManagerService ams;

    /**
     * Called on preRenderView, initializes the data in this object.
     * 
     * @param cse
     */

    public void initialize(final ComponentSystemEvent cse) {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            final HttpServletRequest req = (HttpServletRequest) FacesContext
                    .getCurrentInstance().getExternalContext().getRequest();

            System.out.println("INIT ALERT");
            System.out.println(getId());
            ams.fill(req, this);
        }
    }

    /**
     * Sends a modification request to AlertManagerService for the data in this
     * request.
     * 
     * @return "mainpage" if successful, null if not
     */

    public String modify() {
        final HttpServletRequest req = (HttpServletRequest) FacesContext
                .getCurrentInstance().getExternalContext().getRequest();

        // TODO Validate input!

        final boolean result = ams.modify(req, this);

        if (result) {
            return "mainpage";
        }

        return null;
    }
}
