package dedede.model.data.dtos;

import java.util.Date;

import dedede.model.logic.util.RegisterStatus;
import dedede.model.logic.util.Theme;

public class ApplicationDto {
	
	private String name;

	private String siteNotice;
	
	private String privacyPolicy;

	private String contactInfo;
	
	private byte[] logo;
	
	private String emailRegEx;
	
	private Theme css;
	
	private String lendingStatus;
	
	private Date connectionDeadline;
	
	private Date returnPeriod;
	
	private RegisterStatus registerStatus;
}
