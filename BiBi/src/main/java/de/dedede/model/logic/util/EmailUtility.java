package de.dedede.model.logic.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import de.dedede.model.persistence.util.ConfigReader;
import de.dedede.model.persistence.util.Logger;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

/**
 * Service for sending E-Mails using Jakarta-Mail via SMTP. The invocation of the corresponding email-sending
 * methods are implemented to be asynchronous.
 *
 * @author Ivan Charviakou
 */
public final class EmailUtility {
	
	private static Properties props;
	private static String from;
	private static String username;
	private static String password;

	/**
	 * Sends an email with the given using the given data. The invocation of this method is asynchronous.
	 * 
	 * @param recipient the address of the recipient.
	 * @param subject   subject of the email.
	 * @param body      content of the email.
	 * @throws MessagingException 
	 * @throws AddressException 
	 */
	public static void sendEmail(String recipient, String subject, String body) throws MessagingException {
		new Thread(() -> {
			try {
				sendEmailHelper(recipient, subject, body);
			} catch (MessagingException e) {
				Logger.severe("Couldn't send email message");
			}
		}).start();
	}

	private static void sendEmailHelper(String recipient, String subject, String body) throws MessagingException {
		Session session = Session.getInstance(props, new CustomAuthenticator());
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(from));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
		message.setSubject(subject);
		message.setText(body);
		Transport.send(message);
	}

	/**
	 * Reads and sets the internal properties of this utility from the application's configuration file.
	 * In particular, these are the host, port, whether TLS is used, and whether authentication is required.
	 * After this, the connection is tested to ensure that the supplied properties are valid.
	 *
	 * @return whether or not the connection could be successfully established with the given properties.
	 */
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
			// Since no emails are actually sent, the transport object can be closed in this way
			if (transport != null) { try { transport.close(); } catch (MessagingException ignore) {} }
		}
	}

	/**
	 * Generates a verification link from a passed token which could be sent to a user by email.
	 * The token is encoded using UTF-8, ensuring that any critical characters are escaped.
	 *
	 * @param nav The specific page-id to which the user should be navigated.
	 * @param token	The token, which is attached to the link as a GET-parameter
	 * @return A URL-String, which can be send by email as a link.
	 * @throws UnsupportedEncodingException Is thrown when a problem occurs while encoding the token.
	 */
	public static String getLink(String nav, String token) throws UnsupportedEncodingException {
		ExternalContext e = FacesContext.getCurrentInstance().getExternalContext();
		String url = String.format("%s://%s:%s%s%s", e.getRequestScheme(), e.getRequestServerName(),
				e.getRequestServerPort(), e.getRequestContextPath(), nav);
		return url + "?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
	}
	
	private static class CustomAuthenticator extends Authenticator {
		@Override
		protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password);
        }
	}

}
