package fi.iki.photon.longminder.web;

import java.io.Serializable;
import java.util.Locale;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class Language implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String locale = Locale.getDefault().getDisplayLanguage();

    public void setLocale(String locale1) {
        System.out.println("Locale SET " + locale1);
        this.locale = locale1;
      }

    public synchronized String getLocale() {
        return locale;
    }

    public synchronized String changeLanguage() {
        return null;
    }

}
