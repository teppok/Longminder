package fi.iki.photon.longminder.web;

import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import fi.iki.photon.longminder.entity.dto.AlertDTO;

/**
 * A backing bean for history.xhtml that fetches the alert list and handles
 * delete alert and dismiss alert requests.
 * 
 * @author Teppo Kankaanp‰‰
 * 
 */

@ManagedBean
@RequestScoped
public class HistoryList {
    @EJB
    AlertManagerService ams;

    private List<AlertDTO> history;

    /**
     * Fetches the data from the database to history.
     */

    public void initialize() {
        final HttpServletRequest req = (HttpServletRequest) FacesContext
                .getCurrentInstance().getExternalContext().getRequest();
        if (req != null) {
            setHistory(ams.getAlertsForHistory(req));
        }
    }

    /**
     * Deletes an alert with given id.
     * 
     * @param id
     * @return null
     */

    public String deleteAlert(final String id) {
        System.out.println("H:Delete");
        final HttpServletRequest req = (HttpServletRequest) FacesContext
                .getCurrentInstance().getExternalContext().getRequest();

        ams.deleteAlert(req, Integer.parseInt(id));
        return null;
    }

    /**
     * Dismisses an alert with given id.
     * 
     * @param id
     * @return null
     */

    public String dismiss(final String id) {
        System.out.println("H:Dismiss");
        final HttpServletRequest req = (HttpServletRequest) FacesContext
                .getCurrentInstance().getExternalContext().getRequest();

        ams.dismiss(req, Integer.parseInt(id));
        return null;
    }

    public List<AlertDTO> getHistory() {
        initialize();
        return history;
    }

    public void setHistory(final List<AlertDTO> history) {
        this.history = history;
    }

}
