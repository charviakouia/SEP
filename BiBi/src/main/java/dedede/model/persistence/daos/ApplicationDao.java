package dedede.model.persistence.daos;


import dedede.model.data.dtos.ApplicationDto;

/**
 * This DAO (data access object) is responsible for fetching global application data from
 * a persistent data store as well as saving to it.
 *
 * See the {@link dedede.model.data.dtos.ApplicationDto} class for the used DTO.
 * @author King Dedede
 *
 */
public final  class ApplicationDao {
	
	private ApplicationDto applicationDto;

	/**
	 * Overwrites global application data in the persistent data store.
	 * The enclosed ID is used to remove existing data from the persistent data store.
	 *
	 * @param applicationDto	A DTO container with the overwriting data
	 * @see						dedede.model.data.dtos.ApplicationDto
	 */
	public static void updateCustomizing(ApplicationDto applicationDto) {}

	/**
	 * Fetches the default global application data from the persistent data store.
	 *
	 * @return					A DTO container with the default global application data.
	 * @see						dedede.model.data.dtos.ApplicationDto
	 */
    public static ApplicationDto loadCustomizing() {
    	return null;
	}
    
}
