package fi.iki.photon.longminder;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import fi.iki.photon.longminder.entity.Alert;
import fi.iki.photon.longminder.entity.User;
import fi.iki.photon.longminder.entity.dto.UserDTO;

/**
 * Session Bean implementation class EmailManagerBean
 */
@Stateless
@LocalBean
public class EmailManagerBean implements EmailManager {

	@EJB UserManagerBean um;
	@EJB AlertManagerBean am;
	
	//@Resource(name="mail/longminderMail")
	//private Session mailSession;
	

    /**
     * Default constructor. 
     */
    public EmailManagerBean() {
        // TODO Auto-generated constructor stub
    }

    /** Sends a verification email to the email specified in UserDTO. */
    
	@Override
	public void sendVerificationEmail(UserDTO ud) {
/*		try {
			User u = um.findTrueUser(ud);
			um.createLogin(u);
			if (u.getLogins() == null || u.getLogins().size() == 0) {
				throw new Error("Login should have been created.");
			}
			String key = u.getLogins().get(0).getLoginkey();
			
			Message msg = new MimeMessage(mailSession);
			msg.setFrom(new InternetAddress("Longminder@example.com"));
			msg.setRecipient(Message.RecipientType.TO, new InternetAddress("teppo.kankaanpaa@gmail.com"));
			
			msg.setSubject("Please confirm your email address for Longminder");
			msg.setContent("You can't receive Longminder email notifications before you have confirmed your email address. Please do this by clicking the following link: \n\n" +
					"http://localhost:8080/LongminderWAR/verify.xhtml?key=" + key + "\n\n" + 
					"If you haven't requested this confirmation, please ignore this email.", "text/plain");
			msg.setSentDate(new Date());
			
			Transport.send(msg);
			
		} catch (MessagingException e) {
			// Do nothing
		}
		
*/		
	}

	/** Sends emails for all users in the database whose email has been verified. */
	
	@Schedule(second="0,20,40",minute="*",hour="*")
	public void sendEmails() {
		List<User> users = um.getVerifiedUsers();
		
		for (User u : users) {
			sendAlertEmail(u);
		}
	}

	private void sendAlertEmail(User u) {
		System.out.println(getAlertEmailString(u));
	}
	
	private String getAlertEmailString(User u) {
		List<Alert> alerts = u.getAlerts();

		if (alerts == null || alerts.size() == 0) return null;

		ResourceBundle res = ResourceBundle.getBundle("fi.iki.photon.longminder.Messages");
		
		String alertString = res.getString("email.greeting") + " " + u.getFirstname() +", \n\n" + res.getString("email.alertsdue") + ":\n\n";
		Date now = new Date();
		DateFormat format = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.ENGLISH);
		
		for (Alert a : alerts) {
			if (a.getNextAlert().before(now)) {
				alertString += res.getString("email.alert") + " \"" + a.getDescription() + "\" " + res.getString("email.due") + " " + format.format(a.getNextAlert()) + "\n";
			}
		}
		alertString += "\n" + res.getString("email.ending");
		
		return alertString;
	}
    
}
