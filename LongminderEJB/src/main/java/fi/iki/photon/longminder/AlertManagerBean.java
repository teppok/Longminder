package fi.iki.photon.longminder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import fi.iki.photon.longminder.entity.Alert;
import fi.iki.photon.longminder.entity.User;
import fi.iki.photon.longminder.entity.dto.AlertDTO;
import fi.iki.photon.longminder.entity.dto.UserDTO;

/**
 * AlertManagerBean for interface AlertManager for managing anything
 * Alert related with the Persistence Context.
 */
@Stateless
@LocalBean
public class AlertManagerBean implements AlertManager, Serializable {
	
	private static final long serialVersionUID = 1L;

	@PersistenceContext
    private EntityManager em;

	@EJB UserManagerBean um;
    /**
     * Default constructor. 
     */
    public AlertManagerBean() {
    	// Empty constructor.
    }
    
//    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    /*
	@Override
	public void save(AlertDTO dto) {
//		Alert a = new Alert(dto, um);

//		System.out.println("Saving alert");
//    	System.out.println(a.getOwner());
//    	System.out.println(a.getNextAlert());
//    	System.out.println(a.getDescription());

//   	if (a.getOwner() == null) return;

//		em.persist(a);
	}
	*/

	/** Simple ping for testing. */
	
	public String hello() { return "hello"; }


    /** Given an AlertDTO with a valid Id field, updates the corresponding database object
     * values based on the supplied AlertDTO. Update logic is in Alert.initialize (currently
     * overwrites values, nulls cause exception).
     */
    
//    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Override
	public void update(AlertDTO modify) {
    	Alert a = findTrueAlert(modify.getId());
    	
    	if (a != null) {
    		a.initialize(modify);
    	}
	}

	/**
	 * Given an id of an Alert, returns the corresponding Alert object.
	 * @param id
	 * @return Alert object with this id.
	 */
	
	public Alert findTrueAlert(int id) {
		return em.find(Alert.class, new Integer(id));
	}

	/**
	 * Given an AlertDTO object with a valid Id, returns the corresponding Alert object.
	 * @param adto
	 * @return Alert object with this id.
	 */

	public Alert findTrueAlert(AlertDTO adto) {
		return findTrueAlert(adto.getId());
	}

	/** Given an UserDTO with a valid Id field, adds an alert with values from AlertDTO for this
	 * User.
	 */
	
	@Override
	public void addAlert(UserDTO udto, AlertDTO adto) {
		addAlert(udto.getId(), adto);
	}

	/** Given an user id, adds an alert with values from AlertDTO for this User. */
	
	private void addAlert(int id, AlertDTO adto) {
		System.out.println("Adding alert " + adto.getDescription());
		User u = um.findTrueUser(id);
		if (u != null) {
			Alert a = new Alert(adto);
			u.getAlerts().add(a);
		}
	}

	/** Given an id, returns an AlertDTO object with this Id. */
	
	@Override
	public AlertDTO find(int id) {
		Alert a = em.find(Alert.class, new Integer(id));

		if (a == null) return null;
		
		AlertDTO ad = new AlertDTO();
		a.initializeDTO(ad);
		return ad;
	}

	/** Given an AlertDTO object with a valid Id value, fills its values from
	 * the database.
	 */
	
	@Override
	public void fill(AlertDTO dto) {
		Alert a = findTrueAlert(dto.getId());
		if (a != null) {
			a.initializeDTO(dto);
		}
	}

	/**
	 * Given an id, removes the alert with this id.
	 */
	
//   @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Override
	public void remove(int id) {
		Alert a = findTrueAlert(id);
		if (a != null) {
			em.remove(a);
		}
	}

	/**
	 * Given an UserDTO object, returns a list of AlertDTO objects that
	 * this User has.
	 */
	
	@Override
	public List<AlertDTO> getAlerts(UserDTO user) {
		User u = um.findTrueUser(user);
		if (u == null) return null;

		List<AlertDTO> result = new ArrayList<AlertDTO>();
		
		List<Alert> listResult = u.getAlerts();

		if (listResult == null) return null;

		for (Alert a : listResult) {
			AlertDTO ad = new AlertDTO();
			a.initializeDTO(ad);
			result.add(ad);
		}
		return result;
	}
	
}

