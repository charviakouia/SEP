package de.dedede.model.data.dtos;

/**
 * This DTO (data transfer object) is responsible for aggregating and
 * encapsulating data for searching categories un the category browser.
 * 
 * @author León Liehr
 */
public class CategorySearchDto {

	private String searchTerm;

	/**
	 * Fetch the search term.
	 * 
	 * @return The search term.
	 */
	public String getSearchTerm() {
		return searchTerm;
	}

	/**
	 * Set the search term.
	 * 
	 * @param searchTerm The search term.
	 */
	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}

}
