package fi.iki.photon.longminder;

import static org.junit.Assert.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.NamingException;

import org.junit.BeforeClass;
import org.junit.Test;

import fi.iki.photon.longminder.entity.Alert;
import fi.iki.photon.longminder.entity.User;
import fi.iki.photon.longminder.entity.dto.AlertDTO;
import fi.iki.photon.longminder.entity.dto.UserDTO;

public class AlertManagerBeanTest {

	static AlertManagerBean amb;
	static UserManagerBean umb;
	
	@BeforeClass
	public static void setUp() {
		try { 
		    Map<String, Object> properties = new HashMap<String, Object>();
		    properties.put(EJBContainer.MODULES, new File("target/classes") );
		    properties.put("org.glassfish.ejb.embedded.glassfish.installation.root", 
		            "./src/test/glassfish");
		    EJBContainer ejb = EJBContainer.createEJBContainer(properties);
	//		EJBContainer ejb = EJBContainer.createEJBContainer();
	
		    amb = (AlertManagerBean) ejb.getContext().lookup("java:global/classes/AlarmManagerBean!fi.iki.photon.longminder.AlarmManagerBean");
			umb = (UserManagerBean) ejb.getContext().lookup("java:global/classes/UserManagerBean!fi.iki.photon.longminder.UserManagerBean");
		} catch (NamingException e) {
			e.printStackTrace();
			fail();
		}
//			Userrecord u = umb.find("test");

	}
	
	@Test
	public void testSimpleInsert() {
		try {
			
		umb.removeAll();
		assertEquals("hello", amb.hello());

		Alert a = new Alert(new AlertDTO());
		a.setDescription("Test alarm");
		
		UserDTO ud = new UserDTO();
		ud.setEmail("test");
		ud.setFirst("test");
		ud.setLast("test");
		ud.setPassword1("test");
		ud.setPassword2("test");
		User u = new User();
		
//		umb.save(u);
		
//			u = umb.find("test");
//		a.setOwner(u);
		
//		amb.save(a);
		
//		u = umb.find("test");
			
		} catch (Exception e) {
			e.printStackTrace();
//			throw e;
		}
	}
	
}
