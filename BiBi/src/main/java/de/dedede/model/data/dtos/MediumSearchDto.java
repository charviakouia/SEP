package de.dedede.model.data.dtos;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MediumSearchDto {

	private static final int DEFAULT_NUMBER_OF_NUANCED_SEARCH_QUERIES = 3;

	private String generalSearchTerm;

	private List<NuancedSearchQuery> nuancedSearchQueries = Stream.generate(NuancedSearchQuery::new)
			.limit(DEFAULT_NUMBER_OF_NUANCED_SEARCH_QUERIES).collect(Collectors.toList());

	public String getGeneralSearchTerm() {
		return generalSearchTerm;
	}

	public void setGeneralSearchTerm(String generalSearchTerm) {
		this.generalSearchTerm = generalSearchTerm;
	}

	public List<NuancedSearchQuery> getNuancedSearchQueries() {
		return nuancedSearchQueries;
	}

	public void setNuancedSearchQueries(List<NuancedSearchQuery> nuancedSearchQueries) {
		this.nuancedSearchQueries = nuancedSearchQueries;
	}

	public static class NuancedSearchQuery {

		private SearchOperator operator = SearchOperator.AND;

		private AttributeOrCategory criterion;

		private String term;

		public SearchOperator getOperator() {
			return operator;
		}

		public void setOperator(SearchOperator operator) {
			this.operator = operator;
		}

		public AttributeOrCategory getCriterion() {
			return criterion;
		}

		public void setCriterion(AttributeOrCategory criterion) {
			this.criterion = criterion;
		}

		public String getTerm() {
			return term;
		}

		public void setTerm(String term) {
			this.term = term;
		}

	}

}
