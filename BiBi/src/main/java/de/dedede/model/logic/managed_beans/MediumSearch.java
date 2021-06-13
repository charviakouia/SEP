package de.dedede.model.logic.managed_beans;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.dedede.model.data.dtos.MediumDto;
import de.dedede.model.data.dtos.MediumSearchCriterion;
import de.dedede.model.data.dtos.MediumSearchDto;
import de.dedede.model.data.dtos.MediumSearchDto.NuancedSearchQuery;
import de.dedede.model.data.dtos.SearchOperator;
import de.dedede.model.logic.converters.SearchCriterionConverter;
import de.dedede.model.persistence.util.Logger;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
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
@RequestScoped
public class MediumSearch extends PaginatedList implements Serializable {

	private static final int DEFAULT_NUMBER_OF_NUANCED_SEARCH_QUERIES = 3;

	@Serial
	private static final long serialVersionUID = 1L;

	private MediumSearchDto mediumSearch = new MediumSearchDto();

	{
		mediumSearch.setNuancedSearchQueries(Stream.generate(NuancedSearchQuery::new)
				.limit(DEFAULT_NUMBER_OF_NUANCED_SEARCH_QUERIES).collect(Collectors.toList()));
	}

	private List<MediumDto> mediums;

	private int searchCriteriaRotation = 0;

	@PostConstruct
	public void init() {
	}

	public MediumSearchDto getMediumSearch() {
		return mediumSearch;
	}

	public void setMediumSearch(MediumSearchDto mediumSearch) {
		this.mediumSearch = mediumSearch;
	}

	public List<SearchOperator> getAllSearchOperators() {
		return Arrays.asList(SearchOperator.values());
	}

	public List<MediumSearchCriterion> getAllSearchCriteria() {
		final var criteria = MediumSearchCriterion.values();
//		Collections.rotate(criteria, searchCriteriaRotation);
//		searchCriteriaRotation += 1;
		return criteria;
	}

	/**
	 * Search for the corresp. mediums.
	 */
	public void searchMedium() {
		Logger.development("tmp = " + tmp + (tmp instanceof MediumSearchCriterion.Attribute));
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

	private MediumSearchCriterion tmp = new MediumSearchCriterion.Category();

	public MediumSearchCriterion getTmp() {
		return tmp;
	}

	public void setTmp(MediumSearchCriterion tmp) {
		this.tmp = tmp;
	}

	public SearchCriterionConverter getConv() {
		return new SearchCriterionConverter();
	}

}
