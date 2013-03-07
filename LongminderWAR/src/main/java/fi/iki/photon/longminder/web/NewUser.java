package fi.iki.photon.longminder.web;

import java.io.Serializable;

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
 * @author Teppo Kankaanpää
 * 
 */

@ManagedBean
@RequestScoped
public class NewUser extends UserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    UserManagerService ums;

    /**
     * Request that the new user should be registered.
     * 
     * @return "mainpage" on success, null on fail.
     */

    public String register() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest req = (HttpServletRequest) context
                .getExternalContext().getRequest();

        // TODO Validate input!

        boolean result = ums.register(this, req);

        if (result) {
            return "mainpage";
        }
        FacesMessage msg = new FacesMessage("Registration failed.");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        return null;
    }
}
