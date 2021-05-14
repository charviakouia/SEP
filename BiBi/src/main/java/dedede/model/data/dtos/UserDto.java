package dedede.model.data.dtos;

import dedede.model.logic.util.AccountStatus;
import dedede.model.logic.util.Role;
import dedede.model.logic.util.Token;


public class UserDto {
	
	private int id;
	
	private String firstName;
	
	private String lastName;
	
	private String passwordHash;
	
	private String passwordLostToken;
	
	private String emailAddress;
	
	private boolean isEmailVerified;
	
	private Role role;
	
	private int zipCode;
	
	private String city;
	
	private String street;

	/**
	 * The street number.
	 * This is a string and not a number since we also need to be able to support more
	 * exotic ones like "21a".
	 */
	private String streetNumber;
	
	private int lendingPeriod;
	
	private AccountStatus accountStatus;
	
	private Token token;

}
