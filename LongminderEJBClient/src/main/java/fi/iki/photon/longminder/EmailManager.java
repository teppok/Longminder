package fi.iki.photon.longminder;

import javax.ejb.Remote;

import fi.iki.photon.longminder.entity.dto.UserDTO;

@Remote
public interface EmailManager {

	public boolean requestVerificationEmail(String serverBase, String email);

}
