package fi.iki.photon.longminder;

import java.util.Locale;

import javax.ejb.Local;

import fi.iki.photon.longminder.entity.dto.UserDTO;

@Local
public interface UserManager {
    public String hello();

    public void create(UserDTO u) throws LongminderException;

    public UserDTO find(String email) throws LongminderException;

    public void remove(String email) throws LongminderException;

    public UserDTO findWithLoginKey(String key) throws LongminderException;

    public boolean verify(String key) throws LongminderException;

    public void updateUser(String email, UserDTO u) throws LongminderException;

    public void fill(String email, UserDTO u) throws LongminderException;
    
    public void setLocale(String email, Locale locale);
    
    public Locale getLocale(String email);
}
