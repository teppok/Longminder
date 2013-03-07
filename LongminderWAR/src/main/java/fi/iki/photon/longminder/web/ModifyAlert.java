package fi.iki.photon.longminder.web;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.PhaseEvent;
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
public class ModifyAlert extends AlertDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    AlertManagerService ams;

    /**
     * Called on preRenderView, initializes the data in this object.
     * 
     * @param cse
     */

    public void initialize(ComponentSystemEvent cse) {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            HttpServletRequest req = (HttpServletRequest) FacesContext
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
        HttpServletRequest req = (HttpServletRequest) FacesContext
                .getCurrentInstance().getExternalContext().getRequest();

        // TODO Validate input!

        boolean result = ams.modify(req, this);

        if (result)
            return "mainpage";

        return null;
    }
}
