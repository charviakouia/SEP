package dedede.model.data.dtos;

import java.util.Date;

import dedede.model.logic.util.AccountStatus;
import dedede.model.logic.util.Role;


public class UserDto {
	
	private int userId;
	
	private String firstName;
	
	private String sureName;
	
	private Date returnDeadline;
	
	private String passwordHash;
	
	private String passwordLostToken;
	
	private String email;
	
	private boolean isEmailVerified;
	
	// private Role userRole;
	
	private String street;
	
	private String location;
	
	private int plz;

	private int streetNumber;
	
	private AccountStatus accountStatus;
	

}
