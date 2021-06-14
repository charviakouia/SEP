package de.dedede.model.logic.managed_beans;

import java.util.List;

import de.dedede.model.data.dtos.PaginationDto;

/**
 * An abstraction over paginated lists for multiple backing beans.
 */
public abstract class PaginatedList {

	private PaginationDto paginatedList = new PaginationDto();

	public abstract List<?> getItems();
	
	public abstract void refresh();

	public PaginationDto getPaginatedList() {
		return paginatedList;
	}

	public void setPaginatedList(PaginationDto paginatedList) {
		this.paginatedList = paginatedList;
	}

	/**
	 * Go to the next page.
	 */
	public void goForward() {
//		if (paginatedList.getPageNumber() == paginatedList.getTotalAmountOfRows()) {
//			
//		}
		
		paginatedList.setPageNumber(paginatedList.getPageNumber() + 1);
		refresh();
	}
	
	/**
	 * Go to the previous page.
	 */
	public void goBack() {
		if (paginatedList.getPageNumber() == 0) {
			return;
		}
		
		paginatedList.setPageNumber(paginatedList.getPageNumber() - 1);
		refresh();
	}
}
