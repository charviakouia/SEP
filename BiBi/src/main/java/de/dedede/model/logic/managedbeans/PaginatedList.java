package de.dedede.model.logic.managedbeans;

import java.util.List;

import de.dedede.model.data.dtos.PaginationDto;

/**
 * An abstraction over paginated lists for multiple backing beans.
 */

// @Temporary design
public abstract class PaginatedList {

	protected PaginationDto select;
	
	public abstract List<?> getItems();

	public void setPage(int page) {

	}

	public void getPage(int page) {

	}

	public void forward() {

	}

	public void backward() {

	}
}
