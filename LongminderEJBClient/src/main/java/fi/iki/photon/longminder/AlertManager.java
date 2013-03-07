package fi.iki.photon.longminder;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Remote;

import fi.iki.photon.longminder.entity.dto.AlertDTO;
import fi.iki.photon.longminder.entity.dto.UserDTO;

@Local
public interface AlertManager {
    public boolean addAlert(String email, AlertDTO a);

    public List<AlertDTO> getAlerts(String email);

    public AlertDTO find(String email, int id);

    public void fill(String email, AlertDTO dto);

    public void remove(String email, int id);

    public void update(String email, AlertDTO dto);

    public void dismiss(String email, int id);

    public List<AlertDTO> getAlertsForList(String email);

    public List<AlertDTO> getAlertsForHistory(String email);

}
