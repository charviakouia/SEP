package de.dedede.model.logic.managed_beans;

import java.util.List;

import de.dedede.model.data.dtos.PaginationDto;

/**
 * An abstraction over paginated lists for multiple backing beans.
 */
public abstract class PaginatedList {

	private PaginationDto paginatedList = new PaginationDto();

	/**
	 * Get the list of items or entries of the paginated list.
	 * 
	 * @return The list of items.
	 */
	public abstract List<?> getItems();

	/**
	 * Re-send the query to the lower layer with the updated {@link PaginationDto paginated details}.
	 */
	public abstract void refresh();

	/**
	 * Get the paginated list of a backing bean.
	 * 
	 * @return The paginated list.
	 */
	public PaginationDto getPaginatedList() {
		return paginatedList;
	}

	/**
	 * Set the paginated list of a backing bean.
	 * 
	 * @param paginatedList The paginated list.
	 */
	public void setPaginatedList(PaginationDto paginatedList) {
		this.paginatedList = paginatedList;
	}

	/**
	 * Get the current one-indexed page number of the paginated list.
	 * 
	 * @return The current one-indexed page number.
	 */
	public int getPageNumber() {
		// translating the zero-indexed page number
		return paginatedList.getPageNumber() + 1;
	}

	/**
	 * Set the to be current one-indexed page number of the paginated list.
	 * The page number is going to be clamped between the lowest
	 * possible page number (being {@code 1}) and the highest possible page
	 * number (which depends on the {@link PaginationDto#getTotalAmountOfPages()}).
	 * 
	 * @param pageNumber The to be current one-indexed page number.
	 */
	public void setPageNumber(int pageNumber) {
		if (pageNumber < 1) {
			pageNumber = 1;
		} else if (pageNumber > paginatedList.getTotalAmountOfPages()) {
			pageNumber = paginatedList.getTotalAmountOfPages();
		}

		// translating the one-indexed page number
		paginatedList.setPageNumber(pageNumber - 1);
	}

	/**
	 * Indicates whether the current page is the first page.
	 * 
	 * @return Flag if the current page is the first one.
	 */
	public boolean isFirstPage() {
		return paginatedList.getPageNumber() == 0;
	}
	
	/**
	 * Indicates whether the current page is the last page.
	 * 
	 * @return Flag if the current page is the last one.
	 */
	public boolean isLastPage() {
		return paginatedList.getPageNumber() + 1 == paginatedList.getTotalAmountOfPages();
	}

	/**
	 * Go to a specific page given by the line number inside of the {@link PaginationDto}.
	 */
	public void goToPage() {
		refresh();
	}

	/**
	 * Go to the next page if possible.
	 */
	public void goForward() {
		if (isLastPage()) {
			return;
		}

		paginatedList.setPageNumber(paginatedList.getPageNumber() + 1);
		refresh();
	}

	/**
	 * Go to the previous page if possible.
	 */
	public void goBack() {
		if (isFirstPage()) {
			return;
		}

		paginatedList.setPageNumber(paginatedList.getPageNumber() - 1);
		refresh();
	}
}
