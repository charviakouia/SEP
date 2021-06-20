package de.dedede.model.logic.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Properties;

import de.dedede.model.persistence.util.ConfigReader;
import de.dedede.model.persistence.util.Logger;
import jakarta.faces.component.UIViewRoot;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.NoSuchProviderException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

/**
 * Service for sending E-Mails
 */
public final class EmailUtility {
	
	private static Properties props;
	private static String from;
	private static String username;
	private static String password;

	/**
	 * Sends an email with the given using the given data.
	 * 
	 * @param recipient the address of the recipient.
	 * @param subject   subject of the email.
	 * @param body      content of the email.
	 * @throws MessagingException 
	 * @throws AddressException 
	 */
	public static void sendEmail(String recipient, String subject, String body) throws AddressException, MessagingException {
		Session session = Session.getInstance(props, new CustomAuthenticator());
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(from));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
		message.setSubject(subject);
		message.setText(body);
		Transport.send(message);
	}

	public static boolean initializeConnection() {
		props = new Properties();
		props.put("mail.smtp.auth", ConfigReader.getInstance().getKey("MAIL_AUTH"));
		props.put("mail.smtp.starttls.enable", ConfigReader.getInstance().getKey("MAIL_TLS"));
		props.put("mail.smtp.host", ConfigReader.getInstance().getKey("MAILSERVER_HOST"));
		props.put("mail.smtp.port", ConfigReader.getInstance().getKey("MAILSERVER_PORT"));
		from = ConfigReader.getInstance().getKey("MAIL_SOURCE");
		username = ConfigReader.getInstance().getKey("MAIL_USER");
		password = ConfigReader.getInstance().getKey("MAIL_PASSWORD");
		return testSettings();
	}
	
	private static boolean testSettings() {
		Session session = Session.getInstance(props, new CustomAuthenticator());
		Transport transport = null;
		try {
			transport = session.getTransport("smtp");
			transport.connect();
			return transport.isConnected();
		} catch (MessagingException e){
			return false;
		} finally {
			if (transport != null) { try { transport.close(); } catch (MessagingException ignore) {} }
		}
	}
	
	private static class CustomAuthenticator extends Authenticator {
		@Override
		protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password);
        }
	}
	
	public static String getLink(String nav, String token) throws UnsupportedEncodingException {
		ExternalContext e = FacesContext.getCurrentInstance().getExternalContext();
		// UIViewRoot vr = FacesContext.getCurrentInstance().getViewRoot();
		String url = String.format("%s://%s:%s%s%s", e.getRequestScheme(), e.getRequestServerName(), 
				e.getRequestServerPort(), e.getRequestContextPath(), nav);
		return url + "?token=" + URLEncoder.encode(token, "UTF-8");
	}

}
