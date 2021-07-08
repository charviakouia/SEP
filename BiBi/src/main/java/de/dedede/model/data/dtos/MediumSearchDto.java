package de.dedede.model.data.dtos;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author León Liehr
 */
public class MediumSearchDto {

	private String generalSearchTerm = "";

	private List<NuancedSearchQuery> nuancedSearchQueries = new ArrayList<>();

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

		private MediumSearchCriterion criterion;

		private String term;

		public SearchOperator getOperator() {
			return operator;
		}

		public void setOperator(SearchOperator operator) {
			this.operator = operator;
		}

		public MediumSearchCriterion getCriterion() {
			return criterion;
		}

		public void setCriterion(MediumSearchCriterion criterion) {
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
