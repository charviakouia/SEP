package de.dedede.model.data.dtos;

import java.time.Duration;

import de.dedede.model.logic.util.Theme;
import de.dedede.model.logic.util.RegisterStatus;

/**
 * This DTO (data transfer object) is responsible for aggregating and
 * encapsulating data about the application for transfer.
 * <p>
 * See the {@link de.dedede.model.persistence.daos.ApplicationDao} class to which this DTO is passed.
 *
 * @author Sergei Pravdin
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

    /**
     * Fetches the name of the application.
     *
     * @return A name of the application.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the application.
     *
     * @param name A name of the application.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Fetches the impressum of the application.
     *
     * @return An impressum of the application.
     */
    public String getSiteNotice() {
        return siteNotice;
    }

    /**
     * Sets the impressum of the application.
     *
     * @param siteNotice An impressum of the application.
     */
    public void setSiteNotice(String siteNotice) {
        this.siteNotice = siteNotice;
    }

    /**
     * Fetches the privacy policy of the application.
     *
     * @return A privacy policy of the application.
     */
    public String getPrivacyPolicy() {
        return privacyPolicy;
    }

    /**
     * Sets the privacy policy of the application.
     *
     * @param privacyPolicy A privacy policy of the application.
     */
    public void setPrivacyPolicy(String privacyPolicy) {
        this.privacyPolicy = privacyPolicy;
    }

    /**
     * Fetches the contact information of the application.
     *
     * @return An information to communicate with the contact person of the library.
     */
    public String getContactInfo() {
        return contactInfo;
    }

    /**
     * Sets the contact information of the application.
     *
     * @param contactInfo An information to communicate with the contact person of the library.
     */
    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    /**
     * Fetches the logo of the application.
     *
     * @return the logo of the application.
     */
    public byte[] getLogo() {
        return logo;
    }

    /**
     * Sets the logo of the application.
     *
     * @param logo the logo of the application.
     */
    public void setLogo(byte[] logo) {
        this.logo = logo;
    }

    /**
     * Fetches a regular expression for the email address, allowing only the user with this
     * regex in suffix their email address to sign up to the application.
     *
     * @return regex of email suffix
     */
    public String getEmailAddressSuffixRegEx() {
        return emailAddressSuffixRegEx;
    }

    /**
     * Sets a regular expression for the email address, allowing only the user with this
     * regex in suffix their email address to sign up to the application.
     * Already registered users should not be restricted in their rights to use the app.
     *
     * @param emailAddressSuffixRegEx regex of email suffix
     */
    public void setEmailAddressSuffixRegEx(String emailAddressSuffixRegEx) {
        this.emailAddressSuffixRegEx = emailAddressSuffixRegEx;
    }

    /**
     * Fetches the visual design theme of the application.
     *
     * @return the visual design theme
     * @see Theme
     */
    public Theme getTheme() {
        return theme;
    }

    /**
     * Sets the visual theme for the application from a enumeration of available themes.
     *
     * @param theme the visual design theme
     * @see Theme
     */
    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    /**
     * Fetches the global lending status in the application.
     * The status indicates whether users are allowed to lend copies of the medium.
     *
     * @return the global lending status in the application.
     */
    public String getLendingStatus() {
        return lendingStatus;
    }

    /**
     * Sets the global lending status in the application.
     * The status indicates whether users are allowed to lend copies of the medium.
     *
     * @param lendingStatus the global lending status in the application.
     */
    public void setLendingStatus(String lendingStatus) {
        this.lendingStatus = lendingStatus;
    }

    /**
     * Fetches a global warning period for all mediums that are available to a user.
     * The users are warned after this period has expired if they still have not returned a copy of the medium.
     *
     * @return A medium's global warning period.
     * @see Duration
     */
    public Duration getWarningPeriod() {
        return warningPeriod;
    }

    /**
     * Sets a global warning period for all mediums that are available to a user.
     * The users are warned after this period has expired if they still have not returned a copy of the medium.
     *
     * @param warningPeriod A medium's global warning period.
     * @see Duration
     */
    public void setWarningPeriod(Duration warningPeriod) {
        this.warningPeriod = warningPeriod;
    }

    /**
     * Fetches a global return period for all mediums that are available to a user.
     * The global period for medium return does not override the return period set in MediumDTO or in UserDTO.
     *
     * @return A medium's global return period.
     * @see MediumDto
     * @see UserDto
     */
    public Duration getReturnPeriod() {
        return returnPeriod;
    }

    /**
     * Sets a global return period for all mediums that are available to a user.
     * The global period for medium return does not override the return period set in MediumDTO or in UserDTO.
     *
     * @param returnPeriod A medium's global return period.
     * @see MediumDto
     * @see UserDto
     */
    public void setReturnPeriod(Duration returnPeriod) {
        this.returnPeriod = returnPeriod;
    }

    /**
     * Fetches a global medium's period for pick up any copy of this medium.
     * After this period has expired, the copy becomes available to all users again.
     *
     * @return A medium's global pick up period.
     * @see Duration
     */
    public Duration getPickupPeriod() {
        return pickupPeriod;
    }

    /**
     * Sets a global medium's period for pick up any copy of this medium.
     * After this period has expired, the copy becomes available to all users again.
     *
     * @param pickupPeriod A medium's global pick up period.
     * @see Duration
     */
    public void setPickupPeriod(Duration pickupPeriod) {
        this.pickupPeriod = pickupPeriod;
    }

    /**
     * Fetches the register status of the application indicating whether registration is available for new users.
     *
     * @return The register status of the applicationю
     * @see RegisterStatus
     */
    public RegisterStatus getRegisterStatus() {
        return registerStatus;
    }

    /**
     * Sets the register status of the application indicating whether registration is available for new users.
     * No action is required with already registered users when the status is changed.
     *
     * @param registerStatus The register status of the application.
     * @see RegisterStatus
     */
    public void setRegisterStatus(RegisterStatus registerStatus) {
        this.registerStatus = registerStatus;
    }
}