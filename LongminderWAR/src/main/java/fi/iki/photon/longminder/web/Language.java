package fi.iki.photon.longminder.web;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Locale;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
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
    transient private UserManagerService ums;
    
    private Locale locale = null; // = 

    public Language() {
        System.out.println("Language created!");
    }

    /**
     * Sets the Locale field to correspond the String representation of a locale.
     * 
     * @param locale1
     */

    
    
    public synchronized void setLocale(String locale1) {
        System.out.println("Locale SET " + locale1);
        
        if (locale1 == null) {
            locale = null;
            return;
        }
        this.locale = Locale.forLanguageTag(locale1);
        saveLocale();
    }
    /**
     * Returns the string representation of the Locale field.
     * 
     * @return String representation of the locale.
     */

    public synchronized String getLocale() {
        Locale result = getLocaleObject();
        if (result == null) return null;

        System.out.println("Got locale " + result.toLanguageTag());
        
        return result.toLanguageTag();
    }

    /**
     * If the Locale field is null, initializes its value by asking UserManagerService
     * (which initializes it based on the account, or returns the default locale).
     * 
     * In any case, returns the Locale field value.
     * @return Locale object.
     */
    
    public synchronized Locale getLocaleObject() {
        if (locale != null) return locale;

        final FacesContext context = FacesContext.getCurrentInstance();
        final HttpServletRequest req = (HttpServletRequest) context
                .getExternalContext().getRequest();
        
        locale = ums.getLocale(req);
        
        return locale;
    }

    /**
     * Saves the current locale value to the database for the logged in
     * user.
     */
    
    private void saveLocale() {
        final FacesContext context = FacesContext.getCurrentInstance();
        final HttpServletRequest req = (HttpServletRequest) context
                .getExternalContext().getRequest();

        ums.setLocale(req, locale);
    }
    
    public static String changeLanguage() {
        return null;
    }
    
    public String firstDayOfWeek() {
        Calendar c = Calendar.getInstance(locale);
        switch (c.getFirstDayOfWeek()) {
        case Calendar.MONDAY: return "alert.mon";
        case Calendar.TUESDAY: return "alert.tue";
        case Calendar.WEDNESDAY: return "alert.wed";
        case Calendar.THURSDAY: return "alert.thu";
        case Calendar.FRIDAY: return "alert.fri";
        case Calendar.SATURDAY: return "alert.sat";
        default: return "alert.sun";
        }
    }
}
