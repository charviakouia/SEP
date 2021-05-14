package dedede.model.data.dtos;

import java.time.Duration;

import dedede.model.logic.util.RegisterStatus;
import dedede.model.logic.util.Theme;

/**
 * A class for aggregate and encapsulate data about an application for transfer.
 */
public class ApplicationDto {

	private String name;

	private String siteNotice;

	private String privacyPolicy;

	private String contactInfo;

	private byte[] logo;

	private String emailAddressSuffixRegEx;

	private Theme theme;

	private String lendingStatus;

	private Duration warningPeriod;

	private Duration returnPeriod;

	private Duration pickupPeriod;

	private RegisterStatus registerStatus;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSiteNotice() {
		return siteNotice;
	}

	public void setSiteNotice(String siteNotice) {
		this.siteNotice = siteNotice;
	}

	public String getPrivacyPolicy() {
		return privacyPolicy;
	}

	public void setPrivacyPolicy(String privacyPolicy) {
		this.privacyPolicy = privacyPolicy;
	}

	public String getContactInfo() {
		return contactInfo;
	}

	public void setContactInfo(String contactInfo) {
		this.contactInfo = contactInfo;
	}

	public byte[] getLogo() {
		return logo;
	}

	public void setLogo(byte[] logo) {
		this.logo = logo;
	}

	public String getEmailAddressSuffixRegEx() {
		return emailAddressSuffixRegEx;
	}

	public void setEmailAddressSuffixRegEx(String emailAddressSuffixRegEx) {
		this.emailAddressSuffixRegEx = emailAddressSuffixRegEx;
	}

	public Theme getTheme() {
		return theme;
	}

	public void setTheme(Theme theme) {
		this.theme = theme;
	}

	public String getLendingStatus() {
		return lendingStatus;
	}

	public void setLendingStatus(String lendingStatus) {
		this.lendingStatus = lendingStatus;
	}

	public Duration getWarningPeriod() {
		return warningPeriod;
	}

	public void setWarningPeriod(Duration warningPeriod) {
		this.warningPeriod = warningPeriod;
	}

	public Duration getReturnPeriod() {
		return returnPeriod;
	}

	public void setReturnPeriod(Duration returnPeriod) {
		this.returnPeriod = returnPeriod;
	}

	public Duration getPickupPeriod() {
		return pickupPeriod;
	}

	public void setPickupPeriod(Duration pickupPeriod) {
		this.pickupPeriod = pickupPeriod;
	}

	public RegisterStatus getRegisterStatus() {
		return registerStatus;
	}

	public void setRegisterStatus(RegisterStatus registerStatus) {
		this.registerStatus = registerStatus;
	}
}
