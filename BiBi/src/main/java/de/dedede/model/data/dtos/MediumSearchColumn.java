package de.dedede.model.data.dtos;

/**
 * 
 * @author León Liehr
 */
public enum MediumSearchColumn {
	TITLE, AUTHORS, EDITION, PUBLISHER, CATEGORY;

	@Override
	public String toString() {
		return switch (this) {
		case TITLE -> "medium_attribute.title";
		case AUTHORS -> "medium_attribute.authors";
		case EDITION -> "medium_attribute.edition";
		case PUBLISHER -> "medium_attribute.publisher";
		case CATEGORY -> "category";
		};
	}
}
