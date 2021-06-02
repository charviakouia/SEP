package de.dedede.model.logic.util;

/**
 * Service for sending E-Mails
 */
public final class EmailUtility {

	/**
	 * Sends an email with the given using the given data.
	 * 
	 * @param recipient the address of the recipient.
	 * @param subject   subject of the email.
	 * @param body      content of the email.
	 */
	static void sendEmail(String recipient, String subject, String body) {

	}
	
	/**
	 * This method is called on system start, it reads the config-File and tries to connect to the specified Mail-Server.
	 * 
	 * @return true if the connection was successfully established
	 */
	public static boolean initializeConnection() {
		// TODO Auto-generated method stub
		return false;
	}

}
