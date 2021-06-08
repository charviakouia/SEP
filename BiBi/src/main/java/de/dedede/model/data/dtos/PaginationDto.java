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

	private int pageNumber;

	private int totalAmountOfRows;

	private String sortBy;

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	/**
	 * Fetches a total amount of rows for the page.
	 *
	 * @return A total amount of rows for the page.
	 */
	public int getTotalAmountOfRows() {
		return totalAmountOfRows;
	}

	/**
	 * Sets a total amount of rows for the page.
	 *
	 * @param totalAmountOfRows A total amount of rows for the page.
	 */
	public void setTotalAmountOfRows(int totalAmountOfRows) {
		this.totalAmountOfRows = totalAmountOfRows;
	}
}
