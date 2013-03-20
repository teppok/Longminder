package fi.iki.photon.longminder.web;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import fi.iki.photon.longminder.LongminderException;
import fi.iki.photon.longminder.entity.dto.UserDTO;

/**
 * A backing bean for profile.xhtml page that holds the values concerning the
 * user. Actually extends UserDTO for ease of use of data transfer.
 * 
 * @author Teppo Kankaanp��
 * 
 */

@ManagedBean
@RequestScoped
public class ModifyUser extends UserDTO {

    @EJB
    private UserManagerService ums;

    /**
     * Called on preRenderView - fetches the values for this object from the
     * database.
     */

    public void initialize() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            final FacesContext context = FacesContext.getCurrentInstance();
            final HttpServletRequest req = (HttpServletRequest) context
                    .getExternalContext().getRequest();

            ums.fill(req, this);
        }
    }

    /**
     * Requests a modification of the user data.
     * 
     * @return "mainpage" if success, null if not.
     */

    public String modify() {
        final FacesContext context = FacesContext.getCurrentInstance();
        final HttpServletRequest req = (HttpServletRequest) context
                .getExternalContext().getRequest();

        // TODO Validate data!

        try {
            
            System.out.println("Modify: " + getId());
            ums.modify(this, req);
        } catch (LongminderException e) {
            final FacesMessage msg = new FacesMessage("Profile modification failed: " + e.toString());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        }
    
        return "mainpage";
    }

    /**
     * Requests a resending of verification email.
     * 
     * @return "mainpage" if successful, null if not.
     */

    public String resend() {
        final FacesContext context = FacesContext.getCurrentInstance();
        final HttpServletRequest req = (HttpServletRequest) context
                .getExternalContext().getRequest();

        final boolean result = ums.sendVerificationEmail(req);

        if (result) {
            return "mainpage";
        }

        return null;
    }

}
