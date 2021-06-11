package de.dedede.model.logic.managed_beans;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.dedede.model.data.dtos.AttributeDto;
import de.dedede.model.data.dtos.MediumDto;
import de.dedede.model.data.dtos.MediumSearchDto;
import de.dedede.model.data.dtos.MediumSearchDto.NuancedSearchQuery;
import de.dedede.model.data.dtos.SearchCriterion;
import de.dedede.model.data.dtos.SearchOperator;
import de.dedede.model.persistence.daos.MediumDao;
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

	@Serial
	private static final long serialVersionUID = 1L;

	private MediumSearchDto mediumSearch = new MediumSearchDto();

	private List<AttributeDto> attributes;

	private List<MediumDto> mediums;

	private int searchCriteriaRotation = 0;

	@PostConstruct
	public void init() {
		attributes = MediumDao.readAllMediumAttributes();
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

	public List<SearchCriterion> getAllSearchCriteria() {
		final var attributeCriteria = attributes.stream()
				.map(attribute -> new SearchCriterion.Attribute(attribute.getName()));
		final var criteria = Stream.concat(attributeCriteria, Stream.of(new SearchCriterion.Category()));
//		Collections.rotate(criteria, searchCriteriaRotation);
//		searchCriteriaRotation += 1;
		return criteria.collect(Collectors.toList());
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
