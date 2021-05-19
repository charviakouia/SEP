package de.dedede.model.logic.managedbeans;

import de.dedede.model.data.dtos.PaginationDto;

/**
 * An abstraction over paginated lists for multiple backing beans.
 */

public abstract class PaginatedList {

	private int entries;

	private int page;

	private String sortBy;

	private PaginationDto select;

	public void setPage(int page) {

	}

	public void getPage(int page) {

	}

	public void forward() {

	}

	public void backward() {

	}

	public int getCurrentPage() {
		return page;
	}

	public void setSortBy(String sortBy) {

	}

	public String getSortBy() {
		return sortBy;

	}

	public void setEntries(int entries) {
		this.entries = entries;
	}

	public int getEntries() {
		return entries;
	}
}
