package de.dedede.model.data.dtos;

import de.dedede.model.logic.util.AccountStatus;
import de.dedede.model.logic.util.LendingStatus;
import de.dedede.model.logic.util.Role;
import de.dedede.model.logic.util.Token;

import java.time.Duration;
import java.util.Date;

/**
 * This DTO (data transfer object) is responsible for aggregating and
 * encapsulating data about a user for transfer.
 * <p>
 * See the {@link de.dedede.model.persistence.daos.UserDao} class to which this DTO is passed.
 *
 * @author Sergei Pravdin
 */
public class UserDto {

    private int id;

    private String firstName;

    private String lastName;

    private String passwordHash;

    private String passwordSalt;

    private String emailAddress;

    private boolean isEmailVerified;

    private Role role;

    private int zipCode;

    private String city;

    private String street;

    private String streetNumber;

    private Duration lendingPeriod;

    private AccountStatus accountStatus;

    private LendingStatus lendingStatus;

    private Token token;

    private Date tokenCreation;

    /**
     * Fetches the id of the user.
     *
     * @return An unique ID of the user.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id of the user.
     *
     * @param id An unique ID of the user.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Fetches a first name of the user.
     *
     * @return A first name of the user.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets a first name of the user.
     *
     * @param firstName A first name of the user.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Fetches a last name of the user.
     *
     * @return A last name of the user.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets a last name of the user.
     *
     * @param lastName A last name of the user.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Fetches a hashed password of the user.
     *
     * @return A hashed password of the user.
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Sets a hashed password of the user.
     *
     * @param passwordHash A hashed password of the user.
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * Fetches a email address of the user.
     *
     * @return A email address of the user.
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * Sets a email address of the user.
     * A email address must be valid for verification.
     *
     * @param emailAddress A email address of the user.
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     * Fetches whether the email address is verified or not.
     *
     * @return true, if the email address of the user is verified, otherwise false.
     */
    public boolean isEmailVerified() {
        return isEmailVerified;
    }

    /**
     * Sets whether the email address is verified or not.
     *
     * @param isEmailVerified true, if the email address of the user is verified, otherwise false.
     */
    public void setEmailVerified(boolean isEmailVerified) {
        this.isEmailVerified = isEmailVerified;
    }

    /**
     * Fetches a role of the user.
     *
     * @return A role of the user.
     * @see Role
     */
    public Role getRole() {
        return role;
    }

    /**
     * Sets a role of the user.
     *
     * @param role A role of the user.
     * @see Role
     */
    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Fetches a zip code of the user.
     * The zip code is part of the user's physical address.
     *
     * @return A zip code of the user.
     */
    public int getZipCode() {
        return zipCode;
    }

    /**
     * Sets a zip code of the user.
     * The zip code is part of the user's physical address.
     *
     * @param zipCode A zip code of the user.
     */
    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
    }

    /**
     * Fetches a city of the user.
     * The city is part of the user's physical address.
     *
     * @return A city of the user.
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets a city of the user.
     * The city is part of the user's physical address.
     *
     * @param city A city of the user.
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Fetches a street of the user.
     * The street is part of the user's physical address.
     *
     * @return A street of the user.
     */
    public String getStreet() {
        return street;
    }

    /**
     * Sets a street of the user.
     * The street is part of the user's physical address.
     *
     * @param street A street of the user.
     */
    public void setStreet(String street) {
        this.street = street;
    }

    /**
     * Fetches a street number of the user. The street number is part of the user's physical address.
     * This is a string and not a number since we also need to be able to support more exotic ones like "21a".
     *
     * @return A street number of the user.
     */
    public String getStreetNumber() {
        return streetNumber;
    }

    /**
     * Sets a street number of the user. The street number is part of the user's physical address.
     * This is a string and not a number since we also need to be able to support more exotic ones like "21a".
     *
     * @param streetNumber A street number of the user.
     */
    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    /**
     * Fetches a lending period that the administrator has privately set for the user.
     * This lending period overrides the global lending period in the application,
     * but does not override the private lending period of the medium.
     *
     * @return A lending period for the user.
     */
    public Duration getLendingPeriod() {
        return lendingPeriod;
    }

    /**
     * Sets a lending period for the user.
     * This lending period overrides the global lending period in the application,
     * but does not override the private lending period of the medium.
     *
     * @param lendingPeriod A lending period for the user.
     */
    public void setLendingPeriod(Duration lendingPeriod) {
        this.lendingPeriod = lendingPeriod;
    }

    /**
     * Fetches a account status of the user.
     *
     * @return A account status for the user.
     * @see AccountStatus
     */
    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    /**
     * Sets a account status of the user.
     *
     * @param accountStatus A account status for the user.
     * @see AccountStatus
     */
    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    /**
     * Fetches a token for the user.
     * This is necessary, for example, to verify your e-mail address or recover a password.
     *
     * @return A token for the user.
     * @see Token
     */
    public Token getToken() {
        return token;
    }

    /**
     * Sets a token for the user.
     * This is necessary, for example, to verify your e-mail address or recover a password.
     *
     * @param token A token for the user.
     * @see Token
     */
    public void setToken(Token token) {
        this.token = token;
    }

    /**
     * Fetches a lending status of the user.
     * This affects whether the user can lend out instances of the medium.
     *
     * @return A account status for the user.
     * @see LendingStatus
     */
    public LendingStatus getLendingStatus() {
        return lendingStatus;
    }

    /**
     * Sets a lending status of the user.
     * This affects whether the user can lend out instances of the medium.
     *
     * @param lendingStatus  A account status for the user.
     * @see LendingStatus
     */
    public void setLendingStatus(LendingStatus lendingStatus) {
        this.lendingStatus = lendingStatus;
    }

    /**
     * Fetches the date the token was created.
     *
     * @return the date the token was created.
     * @see Token
     */
    public Date getTokenCreation() {
        return tokenCreation;
    }

    /**
     * Sets the date the token was created.
     *
     * @param tokenCreation the date the token was created.
     * @see Token
     */
    public void setTokenCreation(Date tokenCreation) {
        this.tokenCreation = tokenCreation;
    }

    /**
     * Fetches the salted passwords.
     * This is necessary to avoid a collision when hashing the password.
     *
     * @return salted passwords.
     */
    public String getPasswordSalt() {
        return passwordSalt;
    }

    /**
     * Sets the salted passwords.
     * This is necessary to avoid a collision when hashing the password.
     *
     * @param passwordSalt A salted passwords.
     */
    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }
}