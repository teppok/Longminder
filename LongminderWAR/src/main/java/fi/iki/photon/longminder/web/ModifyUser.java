package fi.iki.photon.longminder.web;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import fi.iki.photon.longminder.entity.dto.UserDTO;

/**
 * A backing bean for profile.xhtml page that holds the values concerning the user.
 * Actually extends UserDTO for ease of use of data transfer.
 * 
 * @author Teppo Kankaanp‰‰
 *
 */

@ManagedBean
@RequestScoped
public class ModifyUser extends UserDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@EJB UserManagerService ums;

	/**
	 * Called on preRenderView - fetches the values for this object from the database.
	 */
	
	public void initialize() {
		if (!FacesContext.getCurrentInstance().isPostback()) {
			FacesContext context = FacesContext.getCurrentInstance();
			HttpServletRequest req = (HttpServletRequest) context.getExternalContext()
								.getRequest();
			
			ums.fill(req, this);
		}
	}

	/**
	 * Requests a modification of the user data.
	 * @return "mainpage" if success, null if not.
	 */
	
	public String modify() {
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest req = (HttpServletRequest) context.getExternalContext()
							.getRequest();

		// TODO Validate data!
		
		System.out.println("Modify: " + getId());
		boolean result = ums.modify(this, req);
		
		if (result) {
			return "mainpage";
		}
		return null;
	}

	/**
	 * Requests a resending of verification email.
	 * 
	 * @return "mainpage" if successful, null if not.
	 */
	
	
	public String resend() {
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest req = (HttpServletRequest) context.getExternalContext()
							.getRequest();

		boolean result = ums.sendVerificationEmail(req);
		
		if (result) {
			return "mainpage";
		}
		
		return null;
	}
	
}
