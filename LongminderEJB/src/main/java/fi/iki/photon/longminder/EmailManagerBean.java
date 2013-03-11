package fi.iki.photon.longminder;

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
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import fi.iki.photon.longminder.entity.Alert;
import fi.iki.photon.longminder.entity.EmailRequest;
import fi.iki.photon.longminder.entity.User;

/**
 * Session Bean implementation class EmailManagerBean
 */
@Stateless
@LocalBean
public class EmailManagerBean implements EmailManager {

    @EJB
    UserManagerBean um;
    @EJB
    AlertManagerBean am;

    @PersistenceContext
    private EntityManager em;

    @Resource(name = "mail/longminderMail")
    private Session mailSession;

    // Set this JDNI resource to null to bypass any email sending.
    // Otherwise, set it to the server root such as "http://localhost:8080/longminder" 
    @Resource(lookup="longminder/rooturl")
    private String serverBase;

    /**
     * Default constructor.
     */
    public EmailManagerBean() {
        // Do nothing.
    }

    @Override
    public boolean requestVerificationEmail(final String server,
            final String email) {
        serverBase = server;

        final User u = um.findTrueUserForEmail(email);

        // Test if we already have requested a verification.
        final EmailRequest test = em.find(EmailRequest.class, u);
        if (test != null) {
            return true;
        }

        final EmailRequest request = new EmailRequest();
        request.setOwner(u);

        em.persist(request);
        return true;
    }

    /** Sends a verification email to the specified email. */

    public boolean sendVerificationEmail(final String email) {
        try {
            final User u = um.findTrueUserForEmail(email);
            um.createLogin(u);
            if (u.getLogins() == null || u.getLogins().size() == 0) {
                throw new Error("Login key should have been created.");
            }
            final String key = u.getLogins().get(0).getLoginkey();

            final Message msg = new MimeMessage(mailSession);
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

        } catch (final MessagingException e) {
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
        System.out.println(serverBase);
        System.out.println("Send emails!");
        final List<User> users = um.getVerifiedUsers();

        for (final User u : users) {
            sendAlertEmail(u);
        }

        if (serverBase != null) {
            final List<EmailRequest> requests = em.createQuery(
                    "SELECT r FROM EmailRequest r", EmailRequest.class)
                    .getResultList();

            for (final EmailRequest e : requests) {
                sendVerificationEmail(e.getOwner().getEmail());
                em.remove(e);
            }
        }
    }

    private void sendAlertEmail(final User u) {
        try {
            final String alertString = getAlertEmailString(u);

            if (alertString == null) {
                System.out.println("Skipping " + u.getEmail());
                return;
            }

            if (serverBase == null) {
                System.out.println(alertString);

                final Calendar alertCal = Calendar.getInstance();
                alertCal.setTime(new Date());
                alertCal.add(Calendar.DAY_OF_MONTH, 3);
                u.setNextEmail(alertCal.getTime());

                return;
            }

            final Message msg = new MimeMessage(mailSession);
            msg.setFrom(new InternetAddress("noreply@example.com"));
            msg.setRecipient(Message.RecipientType.TO,
                    new InternetAddress(u.getEmail()));

            msg.setSubject("You have new Longminder alerts");
            msg.setContent(alertString, "text/plain");
            msg.setSentDate(new Date());

            Transport.send(msg);

            final Calendar alertCal = Calendar.getInstance();
            alertCal.setTime(new Date());
            alertCal.add(Calendar.DAY_OF_MONTH, 3);
            u.setNextEmail(alertCal.getTime());

        } catch (final MessagingException e) {
            e.printStackTrace();
        }
    }

    private String getAlertEmailString(final User u) {
        final List<Alert> alerts = am.getRawAlertsForEmail(u.getEmail());

        if (alerts == null || alerts.size() == 0) {
            return null;
        }

        boolean alertsFired = false;

        final ResourceBundle res = ResourceBundle
                .getBundle("fi.iki.photon.longminder.Messages");

        final StringBuffer alertString = new StringBuffer();
        alertString.append(res.getString("email.greeting") + " "
                + u.getFirstname() + ", \n\n"
                + res.getString("email.alertsdue") + ":\n\n");
        final Date now = new Date();
        final DateFormat format = DateFormat.getDateInstance(DateFormat.MEDIUM,
                new Locale(u.getLocale().substring(0, 2)));

        final List<Alert> newAlertList = new ArrayList<Alert>();

        boolean newAlertsEncountered = false;

        for (Alert a : alerts) {
            boolean newAlertsCreated = true;
            while (newAlertsCreated) {
                newAlertsCreated = false;
                System.out.println(a.getDescription());
                if (a.getNextAlert().before(now)) {
                    System.out.println(a.getDescription());
                    alertString.append(res.getString("email.alert") + " \""
                            + a.getDescription() + "\" "
                            + res.getString("email.due") + " "
                            + format.format(a.getNextAlert()) + "\n");

                    alertsFired = true;
                    final Alert newAlert = a.rotateAlert();

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

        alertString.append("\n" + res.getString("email.ending"));

        if (!alertsFired) {
            return null;
        }

        // If no unfired alerts and next email date is not met, skip sending.
        if (!newAlertsEncountered && u.getNextEmail().after(now)) {
            return null;
        }

        return alertString.toString();
    }
}
