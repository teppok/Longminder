package fi.iki.photon.longminder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import fi.iki.photon.longminder.entity.Alert;
import fi.iki.photon.longminder.entity.User;
import fi.iki.photon.longminder.entity.dto.AlertDTO;

/**
 * AlertManagerBean for interface AlertManager for managing anything Alert
 * related with the Persistence Context.
 */
@Stateless
@LocalBean
public class AlertManagerBean implements AlertManager {

    @PersistenceContext
    private EntityManager em;

    @EJB
    UserManagerBean um;

    /**
     * Default constructor.
     */
    public AlertManagerBean() {
        // Empty constructor.
    }

    /** Simple ping for testing. */

    public String hello() {
        return "hello";
    }

    /**
     * Given an AlertDTO with a valid Id field, updates the corresponding
     * database object values based on the supplied AlertDTO. Update logic is in
     * Alert.initialize (currently overwrites values, nulls cause exception).
     * Also sets fired and dismissed statuses to false.
     */

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Override
    public void update(final String email, final AlertDTO modify) {
        final Alert a = findTrueAlert(email, modify.getId());

        if (a != null) {
            a.initialize(modify);
            a.setFired(false);
            a.setDismissed(false);
        }
    }

    /**
     * Given an id of an Alert, returns the corresponding Alert object.
     * 
     * @param id
     * @return Alert object with this id.
     */

    public Alert findTrueAlert(final String email, final int id) {
        if (email == null) {
            return null;
        }

        final List<Alert> alerts = em
                .createQuery(
                        "SELECT a FROM User u JOIN u.alerts a WHERE u.email = ?1 AND a.id = ?2",
                        Alert.class).setParameter(1, email)
                .setParameter(2, Integer.valueOf(id)).getResultList();

        if (alerts != null && alerts.size() > 0) {
            return alerts.get(0);
        }
        return null;
    }

    /**
     * Given an AlertDTO object with a valid Id, returns the corresponding Alert
     * object.
     * 
     * @param adto
     * @return Alert object with this id.
     */

    public Alert findTrueAlert(final String email, final AlertDTO adto) {
        return findTrueAlert(email, adto.getId());
    }

    /** Given an user id, adds an alert with values from AlertDTO for this User. */

    @Override
    public boolean addAlert(final String email, final AlertDTO adto) {
        System.out.println("Adding alert " + adto.getDescription());
        final User u = um.findTrueUserForEmail(email);
        if (u != null) {
            final Alert a = new Alert(adto);

            if (a.getNextAlert() == null) {
                if (a.getRepeat() != null) {
                    a.setNextAlert(a.getRepeat().nextAlert(new Date()));
                } else {
                    return false;
                }
            }
            u.addAlert(a);
            return true;
        }
        return false;
    }

    public void rotateAlert(final String email, final int id) {
        final Alert a = findTrueAlert(email, id);
        a.rotateAlert();
    }

    /** Given an id, returns an AlertDTO object with this Id. */

    @Override
    public AlertDTO find(final String email, final int id) {
        final Alert a = findTrueAlert(email, id);

        if (a == null) {
            return null;
        }

        final AlertDTO ad = new AlertDTO();
        a.initializeDTO(ad);
        return ad;
    }

    /**
     * Given an AlertDTO object with a valid Id value, fills its values from the
     * database.
     */

    @Override
    public void fill(final String email, final AlertDTO dto) {
        final Alert a = findTrueAlert(email, dto.getId());

        if (a == null) {
            return;
        }

        a.initializeDTO(dto);
    }

    /**
     * Given an id, removes the alert with this id.
     */

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Override
    public void remove(final String email, final int id) {
        final Alert a = findTrueAlert(email, id);
        if (a == null) {
            return;
        }

        em.remove(a);
    }

    /**
     * Given an UserDTO object, returns a list of AlertDTO objects that this
     * User has.
     */

    @Override
    public List<AlertDTO> getAlerts(final String email) {

        final List<Alert> listResult = em
                .createQuery(
                        "SELECT a FROM User u JOIN u.alerts a WHERE u.email = ?1 ORDER BY a.nextAlert DESC",
                        Alert.class).setParameter(1, email).getResultList();

        if (listResult == null) {
            return null;
        }

        final List<AlertDTO> result = new ArrayList<AlertDTO>();

        for (final Alert a : listResult) {
            final AlertDTO ad = new AlertDTO();
            a.initializeDTO(ad);
            result.add(ad);
        }
        return result;
    }

    @Override
    public List<AlertDTO> getAlertsForList(final String email) {
        final List<AlertDTO> result = new ArrayList<AlertDTO>();

        final List<Alert> listResult = em
                .createQuery(
                        "SELECT a FROM User u JOIN u.alerts a WHERE u.email = ?1 AND "
                                + "((a.oneOff = true AND a.fired = false) OR "
                                + "(a.oneOff = false AND a.dismissed = false)) ORDER BY a.nextAlert ASC",
                        Alert.class).setParameter(1, email).getResultList();

        if (listResult == null) {
            return null;
        }

        for (final Alert a : listResult) {
            final AlertDTO ad = new AlertDTO();
            a.initializeDTO(ad);
            result.add(ad);
        }
        return result;
    }

    @Override
    public List<AlertDTO> getAlertsForHistory(final String email) {
        final List<AlertDTO> result = new ArrayList<AlertDTO>();

        final List<Alert> listResult = em
                .createQuery(
                        "SELECT a FROM User u JOIN u.alerts a WHERE u.email = ?1 AND "
                                + "(a.fired = true) ORDER BY a.nextAlert ASC",
                        Alert.class).setParameter(1, email).getResultList();

        if (listResult == null) {
            return null;
        }

        for (final Alert a : listResult) {
            final AlertDTO ad = new AlertDTO();
            a.initializeDTO(ad);
            result.add(ad);
        }
        return result;
    }

    public List<Alert> getRawAlertsForEmail(final String email) {
        return em
                .createQuery(
                        "SELECT a FROM User u JOIN u.alerts a WHERE u.email = ?1 AND "
                                + "((a.oneOff = true AND a.fired = false) OR "
                                + "(a.oneOff = false AND a.dismissed = false)) ORDER BY a.nextAlert ASC",
                        Alert.class).setParameter(1, email).getResultList();

    }

    @Override
    public void dismiss(final String email, final int id) {
        final Alert a = findTrueAlert(email, id);
        if (a != null && !a.isOneOff()) {
            a.setDismissed(true);
        }

    }

}
