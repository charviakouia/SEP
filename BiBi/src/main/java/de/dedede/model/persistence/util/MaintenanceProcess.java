package de.dedede.model.persistence.util;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.dedede.model.data.dtos.MediumCopyUserDto;
import de.dedede.model.logic.util.EmailUtility;
import de.dedede.model.persistence.daos.MediumDao;
import de.dedede.model.persistence.exceptions.LostConnectionException;
import de.dedede.model.persistence.exceptions.MaxConnectionsException;
import de.dedede.model.persistence.exceptions.MediumDoesNotExistException;
import de.dedede.model.persistence.exceptions.UserDoesNotExistException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;

/**
 * Implements a runnable maintenance process as a singleton.
 * When run by a thread, the maintenance process will check
 * for inconsistent data entries in the application's data
 * store with a given frequency.
 *
 */
public final class MaintenanceProcess extends TimerTask implements Runnable {  //To-Do: Innere Klasse statt superklasse
		
	private static MaintenanceProcess instance;
	
	private static Timer timer;
	
	private static int intervalInMillis;
	
	private static String reminderSubject;
	
	private static String reminderEmail;
	
	private MaintenanceProcess() {}
		
	/**
	 * Returns the single instance of the MaintenanceProcess.
	 * 
	 * @return The singleton MaintenanceProcess instance
	 */
	public static synchronized MaintenanceProcess getInstance() {
		if (instance == null) {
			instance = new MaintenanceProcess();
		}
		return instance;
	}
	
	/**
	 * Executes the maintenance process between set intervals.
	 * This method will cause the calling thread to sleep and 
	 * is therefore intended to be executed by a stand-alone 
	 * worker thread.
	 * 
	 */
	@Override
	public void run() {		
		try {
			List<MediumCopyUserDto> doRemind = MediumDao.readDueDateReminders();
			int sent = 0;
			for (MediumCopyUserDto entry : doRemind) {
				String firstname = entry.getUser().getFirstName();
				String lastname = entry.getUser().getLastName();
				String mediumTitle = entry.getMedium().getTitle();
				Timestamp deadline = entry.getCopy().getDeadline();
				LocalDateTime localDateTime = deadline.toLocalDateTime();
				FormatStyle style = FormatStyle.MEDIUM;
				DateTimeFormatter formatter = 
						DateTimeFormatter.ofLocalizedDateTime(style);
				String timeLeft = localDateTime.format(formatter);
				String recipient = entry.getUser().getEmailAddress();
				String body = insertParams(firstname, lastname, mediumTitle,
						timeLeft, reminderEmail);
				EmailUtility.sendEmail(recipient, reminderSubject, body);
				sent++;
			}
			Logger.detailed(sent + " reminder e-mails have been sent during "
					+ "scheduled service execution");
		} catch (LostConnectionException e) {
			String message = "Error with database communication during "
					+ "scheduled service execution";
			Logger.severe(message);
		} catch (MaxConnectionsException e) {
			String message = "Too many connections to database while scheduled "
					+ "services were executed";
			Logger.severe(message);
		} catch (MediumDoesNotExistException e) {
			String message = "Unexpected Error occured during scheduled service"
					+ " execution, a previously existing medium was deleted"
					+ " from the database";
			Logger.severe(message);
		} catch (UserDoesNotExistException e) {
			String message = "Unexpected Error occured during scheduled service"
					+ " execution, a previously existing user was deleted"
					+ " from the database";
			Logger.severe(message);
		} catch (AddressException e) {
			String message = "An automatic email couldn't be sent to the "
					+ "specified address";
			Logger.severe(message);
		} catch (MessagingException e) {
			String message = "An automatic email couldn't be sent.";
			Logger.severe(message);
		} finally {
			Logger.detailed("Ended execution of automated tasks at "
				+ LocalDateTime.now().toString());
		}
	}
	
	/**
	 * Instructs the thread to execute any urgent tasks and shut 
	 * down in a graceful manner. After calling this method once,
	 * it has no effect.
	 * 
	 */
	public static void shutdown() {
		timer.cancel();
		Logger.detailed("Graceful shutdown of automatic processes initiated.");
	}

	/**
	 * Instructs the thread to execute the urgent tasks immidiately
	 * and schedules subsequent executions at a fixed rate.
	 * Delay of one execution will not affect the schedule.
	 * This should only be called after the setup-method!
	 */
	public void startup() {
		timer.scheduleAtFixedRate(instance, 0, intervalInMillis);               //innere klasse
		Logger.detailed("Automatic tasks started, subsequent executions "
				+ "scheduled with a delay of " 
				+ Math.round((intervalInMillis/1000)/60) + " minutes");
	}

	/**
	 * Relays the necessary RessourceBundle Strings from the upper layer down 
	 * here. Also initializes the Timer.
	 * 
	 * @param emailContent the body of the e-mail with 4 placeholders
	 * @param emailSubject the subject to be displayed
	 */
	public void setup(String emailSubject, String emailBody) {
		ConfigReader instance = ConfigReader.getInstance();
		int keyAsIntInMinutes = instance.getKeyAsInt("SCAN_INTERVAL", 20);
		intervalInMillis = keyAsIntInMinutes * 60 * 1000;
		reminderEmail = emailBody;
		reminderSubject = emailSubject;
		timer = new Timer("MaintenanceProcess", true);
		Logger.detailed("MaintenanceProcess setup completed.");
	}
	
	private String insertParams(String param1, String param2, 
			String param3, String param4, String content) {
		MessageFormat messageFormat = new MessageFormat(content);
		Object[] args = {param1, param2, param3, param4};
		String contentWithParam = messageFormat.format(args);
		
		return contentWithParam;
	}
	
}
