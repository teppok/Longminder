package fi.iki.photon.longminder.web;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import fi.iki.photon.longminder.LongminderException;
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

    @ManagedProperty(value="#{language}")
    private Language lang;

    /**
     * Request that the new user should be registered.
     * 
     * @return "mainpage" on success, null on fail.
     */

    public String register() {
        final FacesContext context = FacesContext.getCurrentInstance();
        final HttpServletRequest req = (HttpServletRequest) context
                .getExternalContext().getRequest();

        if (req.getUserPrincipal() != null) {
            final FacesMessage msg = new FacesMessage("Already logged in. Registration failed.");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        }

        this.setLocale(lang.getLocaleObject());
        
        // TODO Validate input!
        try {
            ums.register(this, req);
        } catch (LongminderException e) {
            final FacesMessage msg = new FacesMessage("Registration failed. " + e.toString());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        }
        return "mainpage";
    }

    public Language getLang() {
        return lang;
    }

    public void setLang(Language lang) {
        this.lang = lang;
    }
}
