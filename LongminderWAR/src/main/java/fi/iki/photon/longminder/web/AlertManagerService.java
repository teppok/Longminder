package fi.iki.photon.longminder.web;

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;

import fi.iki.photon.longminder.AlertManager;
import fi.iki.photon.longminder.UserManager;
import fi.iki.photon.longminder.entity.dto.AlertDTO;
import fi.iki.photon.longminder.entity.dto.UserDTO;

/**
 * Bottom layer class that mainly accesses AlertManager EJB.
 * All methods take a HttpServletRequest, which is used to fetch
 * the authentication information that is passed to the EJB.
 * 
 * @author Teppo Kankaanp‰‰
 *
 */

@Stateless
@LocalBean
public class AlertManagerService implements Serializable {

	private static final long serialVersionUID = 505412290274294293L;
	
	@EJB AlertManager am;
	@EJB UserManager um;

	/**
	 * Creates a new alert for the current user.
	 * 
	 * @param req
	 * @param alert
	 * @return true if success
	 */
	
	public boolean create(HttpServletRequest req, AlertDTO alert) {
		return am.addAlert(req.getRemoteUser(), alert);
	}

	/**
	 * Given an AlertDTO containing a valid id value for an alert that this
	 * user already owns, modifies it.
	 * On invalid id, AlertManager decides what to do, currently does nothing.
	 * 
	 * @param req
	 * @param modify
	 * @return true if success
	 */
	
	public boolean modify(HttpServletRequest req, AlertDTO modify) {
		am.update(req.getRemoteUser(), modify);
		
		return true;
	}
	
	/**
	 * Given an AlertDTO instance containing a valid id value for an alert that
	 * this user already owns, fills in the values from the database.
	 * On invalid id, AlertManager decides what to do, currently does nothing.
	 * @param req
	 * @param adto
	 */
	
	public void fill(HttpServletRequest req, AlertDTO adto) {
		am.fill(req.getRemoteUser(), adto);
	}

	/**
	 * Returns all alerts this user has.
	 * @param req
	 * @return List of AlertDTO items
	 */
	
	public List<AlertDTO> getAlerts(HttpServletRequest req) {
		return am.getAlerts(req.getRemoteUser());
	}

	/**
	 * Returns all the alerts from the database that should be shown on the
	 * upcoming alerts list.
	 * 
	 * @param req
	 * @return List of AlertDTO items
	 */
	
	public List<AlertDTO> getAlertsForList(HttpServletRequest req) {
		return am.getAlertsForList(req.getRemoteUser());
	}

	/**
	 * Returns all the alerts from the database that should be shown on the
	 * alert history list.
	 * 
	 * @param req
	 * @return List of AlertDTO items
	 */
	
	public List<AlertDTO> getAlertsForHistory(HttpServletRequest req) {
		return am.getAlertsForHistory(req.getRemoteUser());
	}

	/**
	 * Given an id for an alert that this user owns, deletes the alert.
	 * On invalid id, AlertManager decides what to do, currently does nothing.
	 * @param req
	 * @param id
	 */
	
	public void deleteAlert(HttpServletRequest req, int id) {
		am.remove(req.getRemoteUser(), id);
	}

	/**
	 * Given an id for an alert that this user owns, dismisses the alert.
	 * On invalid id, AlertManager decides what to do, currently does nothing.
	 * @param req
	 * @param id
	 */
	
	public void dismiss(HttpServletRequest req, int id) {
		am.dismiss(req.getRemoteUser(), id);
	}

}
