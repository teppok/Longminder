package fi.iki.photon.longminder;

import javax.ejb.Remote;

@Remote
public interface EmailManager {

    public boolean requestVerificationEmail(String serverBase, String email);

}
