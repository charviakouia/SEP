package de.dedede.model.logic.managed_beans;

import java.util.List;

import de.dedede.model.data.dtos.PaginationDto;
import de.dedede.model.persistence.util.Logger;

/**
 * An abstraction over paginated lists for multiple backing beans.
 */
public abstract class PaginatedList {

	private PaginationDto paginatedList = new PaginationDto();

	public abstract List<?> getItems();

	public PaginationDto getPaginatedList() {
		return paginatedList;
	}

	public void setPaginatedList(PaginationDto paginatedList) {
		this.paginatedList = paginatedList;
	}

	public void goForward() {
		Logger.development("goForward");
	}
	
	public void goBack() {
		Logger.development("goBack");
	}
}
