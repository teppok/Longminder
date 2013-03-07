package fi.iki.photon.longminder.web;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import fi.iki.photon.longminder.entity.dto.UserDTO;
import fi.iki.photon.longminder.entity.dto.AlertDTO;

/**
 * A backing bean for mainpage.xhtml that fetches the alert list and handles
 * delete alert and dismiss alert requests.
 * 
 * @author Teppo Kankaanp��
 * 
 */

@ManagedBean
@RequestScoped
public class AlertList {
    @EJB
    AlertManagerService ams;

    private List<AlertDTO> alerts;

    /**
     * Fetches the data from the database to alerts.
     */

    public void initialize() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            HttpServletRequest req = (HttpServletRequest) FacesContext
                    .getCurrentInstance().getExternalContext().getRequest();
            if (req != null) {
                setAlerts(ams.getAlertsForList(req));
            }
        }
    }

    /**
     * Deletes an alert with given id.
     * 
     * @param id
     * @return null
     */

    public String deleteAlert(String id) {
        System.out.println("Delete " + id);
        HttpServletRequest req = (HttpServletRequest) FacesContext
                .getCurrentInstance().getExternalContext().getRequest();

        ams.deleteAlert(req, Integer.parseInt(id));
        return null;
    }

    /**
     * Dismisses an alert with the given id.
     * 
     * @param id
     * @return null
     */

    public String dismiss(String id) {
        System.out.println("Dismiss " + id);
        HttpServletRequest req = (HttpServletRequest) FacesContext
                .getCurrentInstance().getExternalContext().getRequest();

        ams.dismiss(req, Integer.parseInt(id));
        return null;
    }

    public List<AlertDTO> getAlerts() {
        initialize();
        return alerts;
    }

    public void setAlerts(List<AlertDTO> alerts) {
        this.alerts = alerts;
    }

}
