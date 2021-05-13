package dedede.model.data.dtos;

import java.util.Date;

/**
 * A class for aggregate and encapsulate data about an user for transfer.
 */
public class UserDto {
	
	
	private int userId;
	
	private String firstName;
	
	private String sureName;
	
	private Date returnDeadline;
	
	private String passwordHash;
	
	private String passwordLostToken;
	
	private String email;
	
	private boolean isEmailVerified;
	
	private Role userRole;
	
	private String street;
	
	private String location;
	
	private int plz;
	
	private int hausNumber;
	
	private AvailabilityStatus availabilityStatus;
	
	private AccountStatus accountStatus;
	
	
	
	

}
