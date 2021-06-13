package de.dedede.model.data.dtos;

public enum MediumAttribute {

	TITLE, AUTHORS, TYPE, EDITION, PUBLISHER, YEAR_OF_RELEASE, ISBN, URL, SUMMARY;

	public static MediumAttribute parse(String input) {
		return switch (input) {
		case "medium_attribute.title" -> TITLE;
		case "medium_attribute.authors" -> AUTHORS;
		case "medium_attribute.type" -> TYPE;
		case "medium_attribute.edition" -> EDITION;
		case "medium_attribute.publisher" -> PUBLISHER;
		case "medium_attribute.year_of_release" -> YEAR_OF_RELEASE;
		case "medium_attribute.isbn" -> ISBN;
		case "medium_attribute.url" -> URL;
		case "medium_attribute.summary" -> SUMMARY;
		default -> null;
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
		};
	}
}
