package de.dedede.model.logic.managed_beans;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import de.dedede.model.data.dtos.MediumDto;
import de.dedede.model.data.dtos.MediumSearchDto;
import de.dedede.model.data.dtos.MediumSearchDto.NuancedSearchQuery;
import de.dedede.model.data.dtos.SearchOperator;
import jakarta.annotation.PostConstruct;
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
	
	private MediumSearchDto mediumSearch = new MediumSearchDto();
	
	private List<MediumDto> mediums;
	
	@PostConstruct
	public void init() {
		
	}
	
	public MediumSearchDto getMediumSearch() {
		return mediumSearch;
	}

	public void setMediumSearch(MediumSearchDto mediumSearch) {
		this.mediumSearch = mediumSearch;
	}
	
	public SearchOperator[] getAllSearchOperators() {
		return SearchOperator.values();
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
		mediumSearch.getNuancedSearchQueries().add(new NuancedSearchQuery());
	}

	@Override
	public List<MediumDto> getItems() {
		return mediums;
	}

}


