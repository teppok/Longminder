package fi.iki.photon.longminder.web;

import java.io.Serializable;
import java.util.Locale;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

@Named("language")
@ManagedBean
@SessionScoped
public class Language implements Serializable {
    private static final long serialVersionUID = 1L;

    @EJB
    private UserManagerService ums;
    
    private Locale locale = null; // = 

    public Language() {
        System.out.println("Language created!");
    }
    
    public void setLocale(String locale1) {
        System.out.println("Locale SET " + locale1);
        
        if (locale1 == null) {
            locale = null;
            return;
        }
        this.locale = Locale.forLanguageTag(locale1);
        saveLocale();
      }

    public synchronized String getLocale() {
        if (locale != null) return locale.toLanguageTag();

        final FacesContext context = FacesContext.getCurrentInstance();
        final HttpServletRequest req = (HttpServletRequest) context
                .getExternalContext().getRequest();
        
        locale = ums.getLocale(req);
        
        return locale.toLanguageTag();
    }

    private void saveLocale() {
        final FacesContext context = FacesContext.getCurrentInstance();
        final HttpServletRequest req = (HttpServletRequest) context
                .getExternalContext().getRequest();

        ums.setLocale(req, locale);
    }
    
    public synchronized String changeLanguage() {
        return null;
    }
}
