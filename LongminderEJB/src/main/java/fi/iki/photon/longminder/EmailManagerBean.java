package fi.iki.photon.longminder;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import fi.iki.photon.longminder.entity.Alert;
import fi.iki.photon.longminder.entity.EmailRequest;
import fi.iki.photon.longminder.entity.User;
import fi.iki.photon.longminder.entity.dto.UserDTO;

/**
 * Session Bean implementation class EmailManagerBean
 */
@Stateless
@LocalBean
public class EmailManagerBean implements EmailManager, Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    UserManagerBean um;
    @EJB
    AlertManagerBean am;

    @PersistenceContext
    private EntityManager em;

    @Resource(name = "mail/longminderMail")
    private Session mailSession;

    private boolean ignoreEmail = true;

    // TODO Get serverBase from JDNI or something, instead of a parameter to
    // request.
    private String serverBase = null;

    /**
     * Default constructor.
     */
    public EmailManagerBean() {
        // Do nothing.
    }

    @Override
    public boolean requestVerificationEmail(String server, String email) {
        this.serverBase = server;

        User u = um.findTrueUserForEmail(email);
        EmailRequest request = new EmailRequest();
        request.setOwner(u);

        em.persist(request);
        return true;
    }

    /** Sends a verification email to the specified email. */

    public boolean sendVerificationEmail(String email) {
        try {
            User u = um.findTrueUserForEmail(email);
            um.createLogin(u);
            if (u.getLogins() == null || u.getLogins().size() == 0) {
                throw new Error("Login key should have been created.");
            }
            String key = u.getLogins().get(0).getLoginkey();

            Message msg = new MimeMessage(mailSession);
            msg.setFrom(new InternetAddress("noreply@example.com"));
            msg.setRecipient(Message.RecipientType.TO,
                    new InternetAddress(u.getEmail()));

            msg.setSubject("Please confirm your email address for Longminder");
            msg.setContent(
                    "You can't receive Longminder email notifications before you have confirmed your email address. Please do this by clicking the following link: \n\n"
                            + serverBase
                            + "/verify.xhtml?key="
                            + key
                            + "\n\n"
                            + "If you haven't requested this confirmation, please ignore this email.",
                    "text/plain");
            msg.setSentDate(new Date());

            Transport.send(msg);

        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Sends emails for all users in the database whose email has been verified.
     */

    @Schedule(minute = "*", hour = "*")
    public void sendEmails() {
        System.out.println("Send emails!");
        List<User> users = um.getVerifiedUsers();

        for (User u : users) {
            sendAlertEmail(u);
        }

        if (serverBase != null) {
            List<EmailRequest> requests = em.createQuery(
                    "SELECT r FROM Request r", EmailRequest.class)
                    .getResultList();

            for (EmailRequest e : requests) {
                sendVerificationEmail(e.getOwner().getEmail());
            }
        }
    }

    private void sendAlertEmail(User u) {
        try {
            String alertString = getAlertEmailString(u);

            if (alertString == null) {
                System.out.println("Skipping " + u.getEmail());
                return;
            }

            if (ignoreEmail) {
                System.out.println(alertString);

                Calendar alertCal = Calendar.getInstance();
                alertCal.setTime(new Date());
                alertCal.add(Calendar.DAY_OF_MONTH, 3);
                u.setNextEmail(alertCal.getTime());

                return;
            }

            Message msg = new MimeMessage(mailSession);
            msg.setFrom(new InternetAddress("noreply@example.com"));
            msg.setRecipient(Message.RecipientType.TO,
                    new InternetAddress(u.getEmail()));

            msg.setSubject("You have new Longminder alerts");
            msg.setContent(alertString, "text/plain");
            msg.setSentDate(new Date());

            Transport.send(msg);

            Calendar alertCal = Calendar.getInstance();
            alertCal.setTime(new Date());
            alertCal.add(Calendar.DAY_OF_MONTH, 3);
            u.setNextEmail(alertCal.getTime());

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private String getAlertEmailString(User u) {
        List<Alert> alerts = am.getRawAlertsForEmail(u.getEmail());

        if (alerts == null || alerts.size() == 0)
            return null;

        boolean alertsFired = false;

        ResourceBundle res = ResourceBundle
                .getBundle("fi.iki.photon.longminder.Messages");

        String alertString = res.getString("email.greeting") + " "
                + u.getFirstname() + ", \n\n"
                + res.getString("email.alertsdue") + ":\n\n";
        Date now = new Date();
        DateFormat format = DateFormat.getDateInstance(DateFormat.MEDIUM,
                new Locale(u.getLocale().substring(0, 2)));

        List<Alert> newAlertList = new ArrayList<Alert>();

        boolean newAlertsEncountered = false;

        for (Alert a : alerts) {
            boolean newAlertsCreated = true;
            while (newAlertsCreated) {
                newAlertsCreated = false;
                System.out.println(a.getDescription());
                if (a.getNextAlert().before(now)) {
                    System.out.println(a.getDescription());
                    alertString += res.getString("email.alert") + " \""
                            + a.getDescription() + "\" "
                            + res.getString("email.due") + " "
                            + format.format(a.getNextAlert()) + "\n";

                    alertsFired = true;
                    Alert newAlert = a.rotateAlert();

                    if (!a.isFired()) {
                        newAlertsEncountered = true;
                    }

                    a.setFired(true);

                    if (newAlert != null) {
                        newAlertList.add(newAlert);
                        newAlertsCreated = true;
                        a = newAlert;
                    }
                }
            }
        }
        u.getAlerts().addAll(newAlertList);

        alertString += "\n" + res.getString("email.ending");

        if (!alertsFired)
            return null;

        // If no unfired alerts and next email date is not met, skip sending.
        if (!newAlertsEncountered && u.getNextEmail().after(now)) {
            return null;
        }

        return alertString;
    }
}
