package de.dedede.model.logic.managed_beans;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.dedede.model.data.dtos.PaginationDto;
import de.dedede.model.data.dtos.SortingDirection;

/**
 * An abstraction over paginated lists for multiple backing beans.
 * 
 * The backing bean has to be at least
 * {@link jakarta.enterprise.context.ViewScoped} for the pagination system to be
 * able to keep track of the current page.
 * 
 * @param <Column> TODO
 * 
 * @author León Liehr
 */
public abstract class PaginatedList<Column extends Enum<Column>> {

	private PaginationDto<Column> paginatedList = new PaginationDto<>();

	/**
	 * Get the list of items or entries of the paginated list.
	 * 
	 * @return The list of items.
	 */
	public abstract List<?> getItems();

	/**
	 * Re-send the query to the lower layer with the updated {@link PaginationDto
	 * pagination details}.
	 */
	public abstract void refresh();

	/**
	 * Get the paginated list of a backing bean.
	 * 
	 * @return The paginated list.
	 */
	public final PaginationDto<Column> getPaginatedList() {
		return paginatedList;
	}

	/**
	 * Set the paginated list of a backing bean.
	 * 
	 * @param paginatedList The paginated list.
	 */
	public final void setPaginatedList(PaginationDto<Column> paginatedList) {
		this.paginatedList = paginatedList;
	}

	/**
	 * Get the current one-indexed page number of the paginated list.
	 * 
	 * @return The current one-indexed page number.
	 */
	public final int getPageNumber() {
		// translating the zero-indexed page number
		return paginatedList.getPageNumber() + 1;
	}

	/**
	 * Set the to be current one-indexed page number of the paginated list. The page
	 * number is going to be clamped between the lowest possible page number (being
	 * {@code 1}) and the highest possible page number (which depends on the
	 * {@link PaginationDto#getTotalAmountOfPages()}).
	 * 
	 * @param pageNumber The to be current one-indexed page number.
	 */
	public final void setPageNumber(int pageNumber) {
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
	public final boolean isFirstPage() {
		return paginatedList.getPageNumber() == 0;
	}

	/**
	 * Indicates whether the current page is the last page.
	 * 
	 * @return Flag if the current page is the last one.
	 */
	public final boolean isLastPage() {
		return paginatedList.getPageNumber() + 1 == paginatedList.getTotalAmountOfPages();
	}

	/**
	 * Go to a specific page given by the line number inside of the
	 * {@link PaginationDto}.
	 */
	public final void goToPage() {
		refresh();
	}

	/**
	 * Go to the next page if possible.
	 */
	public final void goForward() {
		if (isLastPage()) {
			return;
		}

		paginatedList.setPageNumber(paginatedList.getPageNumber() + 1);
		refresh();
	}

	/**
	 * Go to the previous page if possible.
	 */
	public final void goBack() {
		if (isFirstPage()) {
			return;
		}

		paginatedList.setPageNumber(paginatedList.getPageNumber() - 1);
		refresh();
	}

	public final void sort(Column column) {
		if (paginatedList.getSortingDirection() == null) {
			paginatedList.setSortingDirection(SortingDirection.ASCENDING);
		} else {
			paginatedList.setSortingDirection(paginatedList.getSortingDirection().inverted());
		}
		
		if (column != paginatedList.getColumnToSortBy()) {
			paginatedList.setSortingDirection(SortingDirection.ASCENDING);
		}
		paginatedList.setColumnToSortBy(column);
		refresh();
	}

}
