package de.dedede.model.logic.managed_beans;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import de.dedede.model.data.dtos.MediumDto;
import de.dedede.model.data.dtos.MediumSearchColumn;
import de.dedede.model.data.dtos.MediumSearchCriterion;
import de.dedede.model.data.dtos.MediumSearchDto;
import de.dedede.model.data.dtos.MediumSearchDto.NuancedSearchQuery;
import de.dedede.model.data.dtos.SearchOperator;
import de.dedede.model.persistence.daos.MediumDao;
import jakarta.annotation.PostConstruct;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
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
public class MediumSearch extends PaginatedList<MediumSearchColumn> implements Serializable {

	// @Task doc
	public static final String GENERAL_SEARCH_TERM_PARAMETER_NAME = "medium-search-term";
	
	private static final int NUANCED_SEARCH_QUERIES_DEFAULT_AMOUNT = 3;

	@Serial
	private static final long serialVersionUID = 1L;
	
	@Inject
	private ExternalContext ectx;

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
		
		if (ectx.getFlash().containsKey(GENERAL_SEARCH_TERM_PARAMETER_NAME)) {
			final var generalSearchTerm = (String)ectx.getFlash().get(GENERAL_SEARCH_TERM_PARAMETER_NAME);
			mediumSearch.setGeneralSearchTerm(generalSearchTerm);
			searchMedia();
		}
		
	}

	public MediumSearchDto getMediumSearch() {
		return mediumSearch;
	}

	public void setMediumSearch(MediumSearchDto mediumSearch) {
		this.mediumSearch = mediumSearch;
	}

	/**
	 * Search for the corresponding mediums.
	 */
	public void searchMedia() {
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

	// @Task use lists/iterators/???
	public String formatAuthors(MediumDto medium) {
		final var authors = new StringBuilder();

		final var author1Exists = medium.getAuthor1() != null && !medium.getAuthor1().trim().isEmpty();
		if (author1Exists) {
			authors.append(medium.getAuthor1());
		}

		if (medium.getAuthor2() != null && !medium.getAuthor2().trim().isEmpty()) {
			if (author1Exists) {
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

	@Override
	public void refresh() {
		searchMedia();
	}
}
