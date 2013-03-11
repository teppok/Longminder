package fi.iki.photon.longminder.web;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import fi.iki.photon.longminder.entity.dto.UserDTO;

/**
 * A backing bean for register.xhtml.
 * 
 * @author Teppo Kankaanp��
 * 
 */

@ManagedBean
@RequestScoped
public class NewUser extends UserDTO {

    @EJB
    private UserManagerService ums;

    /**
     * Request that the new user should be registered.
     * 
     * @return "mainpage" on success, null on fail.
     */

    public String register() {
        final FacesContext context = FacesContext.getCurrentInstance();
        final HttpServletRequest req = (HttpServletRequest) context
                .getExternalContext().getRequest();

        // TODO Validate input!

        final boolean result = ums.register(this, req);

        if (result) {
            return "mainpage";
        }
        final FacesMessage msg = new FacesMessage("Registration failed.");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        return null;
    }
}
