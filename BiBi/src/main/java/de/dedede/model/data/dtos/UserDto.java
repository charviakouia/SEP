package dedede.model.data.dtos;

import dedede.model.logic.util.AccountStatus;
import dedede.model.logic.util.LendingStatus;
import dedede.model.logic.util.Role;
import dedede.model.logic.util.Token;

import java.time.Duration;
import java.util.Date;

/**
 * A class for aggregate and encapsulate data about an user for transfer.
 */
public class UserDto {

	private int id;

	private String firstName;

	private String lastName;

	private String passwordHash;

	//
	private String passwordSalt;

	private String emailAddress;

	private boolean isEmailVerified;

	private Role role;

	private int zipCode;

	private String city;

	private String street;

	/**
	 * The street number. This is a string and not a number since we also need to be
	 * able to support more exotic ones like "21a".
	 */
	private String streetNumber;

	//Muss es vllt. Duration sein?
	private Duration lendingPeriod;

	private AccountStatus accountStatus;

	//enum fehlt
	private LendingStatus lendingStatus;

	private Token token;

	//neu
	private Date tokenCreation;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public boolean isEmailVerified() {
		return isEmailVerified;
	}

	public void setEmailVerified(boolean isEmailVerified) {
		this.isEmailVerified = isEmailVerified;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public int getZipCode() {
		return zipCode;
	}

	public void setZipCode(int zipCode) {
		this.zipCode = zipCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getStreetNumber() {
		return streetNumber;
	}

	public void setStreetNumber(String streetNumber) {
		this.streetNumber = streetNumber;
	}

	public Duration getLendingPeriod() {
		return lendingPeriod;
	}

	public void setLendingPeriod(Duration lendingPeriod) {
		this.lendingPeriod = lendingPeriod;
	}

	public AccountStatus getAccountStatus() {
		return accountStatus;
	}

	public void setAccountStatus(AccountStatus accountStatus) {
		this.accountStatus = accountStatus;
	}

	public Token getToken() {
		return token;
	}

	public void setToken(Token token) {
		this.token = token;
	}

	public LendingStatus getLendingStatus() {
		return lendingStatus;
	}

	public void setLendingStatus(LendingStatus lendingStatus) {
		this.lendingStatus = lendingStatus;
	}

	public Date getTokenCreation() {
		return tokenCreation;
	}

	public void setTokenCreation(Date tokenCreation) {
		this.tokenCreation = tokenCreation;
	}
}
