package de.dedede.model.persistence.daos;

import java.util.List;

import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.persistence.exceptions.EntityInstanceDoesNotExistException;
import de.dedede.model.persistence.exceptions.EntityInstanceNotUniqueException;

/**
 * This DAO (data access object) manages data and privileges for users, who are 
 * identified by their email addresses. In particular, this DAO implements the 
 * CRUD operations (create, read, update, delete).
 * 
 * See the {@link UserDto} class for the used DTO.
 * 
 */
public final class UserDao {

	private UserDao() {}

	/**
	 * Enters new user data into the persistent data store.
	 * The enclosed email address must not be associated with an existing
	 * user in the data store. Otherwise, an exception is thrown.
	 * 
	 * @param userDto A DTO container with the new user data.
	 * @throws EntityInstanceNotUniqueException Is thrown when the enclosed ID
	 * 		is already associated with an existing data entry.
	 */
	public static void createUser(UserDto userDto) 
			throws EntityInstanceNotUniqueException {
	}
	
	/**
	 * Fetches user data associated with a given email. The email address
	 * must be associated with a user in the data store. Otherwise,
	 * an exception is thrown.
	 * 
	 * @param email A DTO container with the email address that identifies the 
	 * 		user to be fetched.
	 * @return A DTO container with the fetched user.
	 * @throws EntityInstanceDoesNotExistException Is thrown when the enclosed
	 * 		email address isn't associated with an existing data entry.
	 */
	public static UserDto readUserByEmail(UserDto userDto) 
			throws EntityInstanceDoesNotExistException {
		return null;
	}
	
	public static List<UserDto> readUsersBySearchCriteria(){
		return null;
		
	}
	
	/**
	 * Overwrites user data associated with a given email. The email address
	 * must be associated with a user in the data store. Otherwise,
	 * an exception is thrown.
	 * 
	 * @param userDto A DTO container with the overwriting user data.
	 * @throws EntityInstanceDoesNotExistException Is thrown when the enclosed
	 * 		email address isn't associated with an existing data entry.
	 */
	public static void updateUser(UserDto userDto) 
			throws EntityInstanceDoesNotExistException {
	}

	/**
	 * Deletes user data associated with a given email address. The email
	 * address must be associated with a user in the data store. Otherwise,
	 * an exception is thrown.
	 * 
	 * @param email A DTO container with an email address identifying the user 
	 * 		to be deleted.
	 * @throws EntityInstanceDoesNotExistException Is thrown when the enclosed
	 * 		email address isn't associated with an existing data entry.
	 * @return A DTO container with data from the deleted user.
	 */
	public static UserDto deleteUser(UserDto userDto) 
			throws EntityInstanceDoesNotExistException {
		return null;
	}

}
