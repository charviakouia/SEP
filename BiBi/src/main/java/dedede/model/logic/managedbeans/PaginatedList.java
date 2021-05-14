package dedede.model.logic.managedbeans;

import dedede.model.data.dtos.PaginationDto;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

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
