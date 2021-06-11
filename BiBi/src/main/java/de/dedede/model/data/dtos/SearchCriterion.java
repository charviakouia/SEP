package de.dedede.model.data.dtos;

public abstract class SearchCriterion {

	public static class Attribute extends SearchCriterion {

		private String value;

		public Attribute(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

	}

	public static class Category extends SearchCriterion {

	}

	// @Question Signature as well?
	
	@Override
	public String toString() {
		if (this instanceof SearchCriterion.Attribute) {
			final var attribute = (SearchCriterion.Attribute) this;
			
			return attribute.getValue();
		} else {
			return "KATEGORIE";
		}
	}
}
