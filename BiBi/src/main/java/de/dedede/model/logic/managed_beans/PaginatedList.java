package de.dedede.model.logic.managed_beans;

import java.util.List;

import de.dedede.model.data.dtos.PaginationDto;

/**
 * An abstraction over paginated lists for multiple backing beans.
 */

// @Temporary design
public abstract class PaginatedList {

	private PaginationDto paginatedList;

	public abstract List<?> getItems();

	public PaginationDto getPaginatedList() {
		return paginatedList;
	}

	public void setPaginatedList(PaginationDto paginatedList) {
		this.paginatedList = paginatedList;
	}

}
