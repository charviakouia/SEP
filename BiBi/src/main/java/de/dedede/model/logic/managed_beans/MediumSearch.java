package de.dedede.model.logic.managed_beans;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import de.dedede.model.data.dtos.MediumDto;
import de.dedede.model.data.dtos.MediumSearchCriterion;
import de.dedede.model.data.dtos.MediumSearchDto;
import de.dedede.model.data.dtos.MediumSearchDto.NuancedSearchQuery;
import de.dedede.model.data.dtos.SearchOperator;
import de.dedede.model.persistence.daos.MediumDao;
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

	private static final int NUANCED_SEARCH_QUERIES_DEFAULT_AMOUNT = 3;
	
	@Serial
	private static final long serialVersionUID = 1L;

	private int searchCriteriaOffset = 0;
	
	private MediumSearchDto mediumSearch = new MediumSearchDto();

	{
		for (var index = 0; index < NUANCED_SEARCH_QUERIES_DEFAULT_AMOUNT; index += 1) {
			addNuancedSearchQuery();
		}
	}

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

	public MediumSearchCriterion[] getAllSearchCriteria() {
		return MediumSearchCriterion.values();
	}

	/**
	 * Search for the corresp. mediums.
	 */
	public void searchMedium() {
		mediums = MediumDao.searchMedia(mediumSearch, getPaginatedList());
	}

	/**
	 * Add a nuanced search query which is the grouping of an operator, a criterion
	 * and a search term.
	 */
	public void addNuancedSearchQuery() {
		final var nuancedSearchQuery = new NuancedSearchQuery();
		nuancedSearchQuery.setCriterion(
				MediumSearchCriterion.values()[searchCriteriaOffset % MediumSearchCriterion.values().length]);
		searchCriteriaOffset += 1;

		mediumSearch.getNuancedSearchQueries().add(nuancedSearchQuery);
	}
	
	// @Task use lists/iterators/…
	public String formatAuthors(MediumDto medium) {
		final var authors = new StringBuilder();
		
		if (medium.getAuthor1() != null) {
			authors.append(medium.getAuthor1());
		}
		
		if (medium.getAuthor2() != null) {
			if (medium.getAuthor1() != null) {
				authors.append(", ");
			}
			
			authors.append(medium.getAuthor2());
		}
		
		return authors.toString();
	}

	@Override
	public List<MediumDto> getItems() {
		return mediums;
	}

}
