package dedede.model.data.dtos;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class PaginationDto implements Serializable {

	private static final long serialVersionID = 1L;

	private final Map<String, String> filterCriteria = new HashMap<>();

	private int pageSize;

	private int page;

	private int totalAmountOfRows;

	private String sortBy;

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getTotalAmountOfRows() {
		return totalAmountOfRows;
	}

	public void setTotalAmountOfRows(int totalAmountOfRows) {
		this.totalAmountOfRows = totalAmountOfRows;
	}

	public static long getSerialversionid() {
		return serialVersionID;
	}

	public Map<String, String> getFilterCriteria() {
		return filterCriteria;
	}

	public int getPage() {
		return page;
	}

	public PaginationDto() {

	}

	public void nextPage() {

	}

	public void setPage(int page) {

	}

	public void previousPage() {

	}

	public int getCurrentPage() {
		return page;
	}

	public void setSortBy(String sortBy) {

	}

	public String getSortBy() {
		return sortBy;

	}

}
