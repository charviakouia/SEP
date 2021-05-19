package de.dedede.model.data.dtos;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * This DTO (data transfer object) is responsible for aggregating and
 * encapsulating data about the pagination of the page for transfer.
 *
 * @author Sergei Pravdin
 */
public class PaginationDto implements Serializable {

	private static final long serialVersionID = 1L;

	private final Map<String, String> filterCriteria = new HashMap<>();

	private int pageSize;

	private int page;

	private int totalAmountOfRows;

	private String sortBy;

	/**
	 * Fetches size of the page.
	 *
	 * @return A size of the page.
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * Sets size of the page.
	 *
	 * @param pageSize A size of the page.
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
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

	/**
	 * Fetches a serial version ID for the page.
	 *
	 * @return A serial version ID for the page.
	 */
	public static long getSerialVersionID() {
		return serialVersionID;
	}

	/**
	 * Fetches a filter criteria for the pagination.
	 *
	 * @param key Filter criteria ID.
	 * @return Criteria value according to which page is filtered.
	 */
	public String getFilterCriteria(String key) {
		return filterCriteria.get(key);
	}

	/**
	 * Sets a filter criteria for the pagination.
	 *
	 * @param key Filter criteria ID.
	 * @param value Criteria value according to which page is filtered.
	 */
	public String setFilterCriteria(String key, String value) {
		return filterCriteria.put(key, value);
	}

	//Was ist das?
	public int getPage() {
		return page;
	}

}
