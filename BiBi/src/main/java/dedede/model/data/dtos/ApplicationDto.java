package dedede.model.data.dtos;

import backing_beans.Impressum;

import java.util.Date;

/**
 * A class for aggregate and encapsulate data about an application for transfer.
 */
public class ApplicationDto {
	
	
	private String applicationName;
	
	private Impressum siteNotice;
	
	private String privacyPolcy;
	
	private byte[] logo;
	
	private String emailRegEx;
	
	private Theme css;
	
	private String lendingStaus;
	
	private Date connectionDeadline;
	
	private Date returnPeriod;
	
	private RegisterStatus registerStatus;

	private UserDto userDto1;
	
	
	


}
