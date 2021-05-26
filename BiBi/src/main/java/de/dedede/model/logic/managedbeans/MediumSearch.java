package de.dedede.model.logic.managedbeans;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import de.dedede.model.data.dtos.MediumDto;
import de.dedede.model.data.dtos.MediumSearchDto;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

/**
 * Backing bean for the medium search page. This page permits users to search
 * for mediums given some of their attributes as search criteria. Further, a
 * user can search mediums by category and by the signature of one of their
 * copies. The page has an optional query parameter corresponding to the search
 * term. If it is not provided, nothing will be searched and the paginated list
 * of results will not be displayed at all.
 */
@Named
@ViewScoped
public class MediumSearch extends PaginatedList implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;
	
	private MediumSearchDto mediumSearch;
	
	private List<MediumDto> mediums;
	
	public MediumSearchDto getMediumSearch() {
		return mediumSearch;
	}

	public void setMediumSearch(MediumSearchDto mediumSearch) {
		this.mediumSearch = mediumSearch;
	}

	/**
	 * Search for the corresp. mediums.
	 */
	public void searchMedium() {

	}

	/**
	 * Add a nuanced search query which is the grouping of an operator, a criterion
	 * and a search term.
	 */
	public void addNuancedSearchQuery() {

	}

	@Override
	public List<MediumDto> getItems() {
		return mediums;
	}

}


