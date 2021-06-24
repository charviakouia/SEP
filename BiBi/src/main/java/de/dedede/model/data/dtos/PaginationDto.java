package de.dedede.model.data.dtos;

import java.io.Serial;
import java.io.Serializable;

/**
 * This DTO (data transfer object) is responsible for aggregating and
 * encapsulating data about the pagination of the page for transfer.
 *
 * @author Sergei Pravdin
 */
public class PaginationDto implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	// zero-indexed
	private int pageNumber = 0;

	private int totalAmountOfPages = 1;
	
	private int totalAmountOfRows = 0;

	private String sortBy;

	/**
	 * Get the current zero-indexed page number.
	 * 
	 * @return The current zero-indexed page number.
	 */
	public int getPageNumber() {
		return pageNumber;
	}

	/**
	 * Set the current zero-indexed page number.
	 * 
	 * @param pageNumber The to-be-current zero-indexed page number.
	 */
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	/**
	 * Get the total amount of pages of the paginated list.
	 * 
	 * The amount is always larger than zero.
	 * 
	 * @return The total amount of pages.
	 */
	public int getTotalAmountOfPages() {
		return totalAmountOfPages;
	}

	/**
	 * Set the total amount of pages of the paginated list.
	 * The amount has to be at least 1.
	 * 
	 * @param totalAmountOfPages The total amount of pages.
	 */
	public void setTotalAmountOfPages(int totalAmountOfPages) {
		if (totalAmountOfPages < 1) {
			throw new IllegalArgumentException("totalAmountOfPages is lower than 1: " + totalAmountOfPages);
		}
		
		this.totalAmountOfPages = totalAmountOfPages;
	}

	public int getTotalAmountOfRows() {
		return totalAmountOfRows;
	}

	public void setTotalAmountOfRows(int totalAmountOfRows) {
		this.totalAmountOfRows = totalAmountOfRows;
	}

	public String getSortBy() {
		return sortBy;
	}

	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}
}
