package de.dedede.model.persistence.util;

import de.dedede.model.data.dtos.PaginationDto;

/**
 * Aggregate of common operations on paginated lists or more specifically on
 * {@link PaginationDto}.
 * 
 * @author León Liehr
 *
 */
public final class Pagination {

	private Pagination() {
		throw new IllegalStateException("Attempt to construct the utility class Pagination");
	}

	/**
	 * Get the number of entries per page of a paginated list.
	 * 
	 * @return The number of entries per page.
	 */
	public static int getEntriesPerPage() {
		return ConfigReader.getInstance().getKeyAsInt("MAX_PAGES");
	}

	/**
	 * Update the current page number and the total amount of pages of given
	 * {@link PaginationDto pagination details}.
	 * 
	 * @param pagination The pagination details to be updated.
	 * @param pageCount The amount of pages as fetched from the database.
	 */
	public static void updatePagination(PaginationDto pagination, int pageCount) {
		pagination.setTotalAmountOfPages(ceilDiv(pageCount, getEntriesPerPage()));
		pagination.setPageNumber(Math.min(pagination.getPageNumber(), pagination.getTotalAmountOfPages() - 1));
	}
	
	/**
	 * Integer division rounding up to the next integer.
	 * 
	 * The standard integer division rounds down, consider {@code 7 / 2 == 3}. This
	 * method on the other hand results in {@code 4} for the arguments {@code 7} and
	 * {@code 2}.
	 * 
	 * @param divident The divident.
	 * @param divisor  The divisor.
	 * @return The rounded-up quotient of the division.
	 */
	private static int ceilDiv(int divident, int divisor) {
		return 1 + ((divident - 1) / divisor);
	}

	/**
	 * Translate the page number to an offset usable inside of an SQL query.
	 * 
	 * @param paginationDto The container with the page number.
	 * @return An offset into the data table.
	 */
	public static int pageOffset(PaginationDto pagination) {
		return pagination.getPageNumber() * getEntriesPerPage();
	}
}
