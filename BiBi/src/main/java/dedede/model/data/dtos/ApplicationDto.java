package dedede.model.data.dtos;

import java.util.Date;

import dedede.model.logic.managedbeans.SiteNotice;
import dedede.model.logic.util.RegisterStatus;
import dedede.model.logic.util.Theme;

public class ApplicationDto {
	
	
	private String applicationName;
	
	private SiteNotice siteNotice;
	
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
