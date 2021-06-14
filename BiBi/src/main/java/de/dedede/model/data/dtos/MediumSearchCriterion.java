package de.dedede.model.data.dtos;

public enum MediumSearchCriterion {

	TITLE, AUTHORS, TYPE, EDITION, PUBLISHER, YEAR_OF_RELEASE, ISBN, URL, SUMMARY, CATEGORY, SIGNATURE;

	public boolean isGeneralSearchCriterion() {
		return switch (this) {
		case TYPE, EDITION, URL, SUMMARY, SIGNATURE -> false;
			default -> true;
		};
	}
	
	@Override
	public String toString() {
		return switch (this) {
		case TITLE -> "medium_attribute.title";
		case AUTHORS -> "medium_attribute.authors";
		case TYPE -> "medium_attribute.type";
		case EDITION -> "medium_attribute.edition";
		case PUBLISHER -> "medium_attribute.publisher";
		case YEAR_OF_RELEASE -> "medium_attribute.year_of_release";
		case ISBN -> "medium_attribute.isbn";
		case URL -> "medium_attribute.url";
		case SUMMARY -> "medium_attribute.summary";
		case CATEGORY -> "category";
		case SIGNATURE -> "signature";
		};
	}
}
