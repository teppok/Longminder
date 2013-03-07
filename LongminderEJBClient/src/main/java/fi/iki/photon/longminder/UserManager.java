package fi.iki.photon.longminder;

import javax.ejb.Local;

import fi.iki.photon.longminder.entity.dto.UserDTO;

@Local
public interface UserManager {
    public String hello();

    public boolean create(UserDTO u);

    public UserDTO find(String email);

    public void remove(String email);

    public UserDTO findWithLoginKey(String key);

    public boolean verify(String key);

    public boolean updateUser(String email, UserDTO u);

    public void fill(String email, UserDTO u);
}
