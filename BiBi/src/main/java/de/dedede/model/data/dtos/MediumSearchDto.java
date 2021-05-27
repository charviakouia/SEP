package de.dedede.model.data.dtos;

import java.util.ArrayList;

public class MediumSearchDto {

	private String generalSearchTerm;

	private ArrayList<NuancedSearchQuery> nuancedSearchQueries;

	public String getGeneralSearchTerm() {
		return generalSearchTerm;
	}

	public void setGeneralSearchTerm(String generalSearchTerm) {
		this.generalSearchTerm = generalSearchTerm;
	}

	public ArrayList<NuancedSearchQuery> getNuancedSearchQueries() {
		return nuancedSearchQueries;
	}

	public void setNuancedSearchQueries(ArrayList<NuancedSearchQuery> nuancedSearchQueries) {
		this.nuancedSearchQueries = nuancedSearchQueries;
	}

	public static class NuancedSearchQuery {

		private SearchOperator operator;

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
