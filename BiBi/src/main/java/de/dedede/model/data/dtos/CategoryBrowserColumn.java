package de.dedede.model.data.dtos;

/**
 * 
 * @author León Liehr
 */
public enum CategoryBrowserColumn {
	TITLE, AUTHORS, EDITION, PUBLISHER;

	@Override
	public String toString() {
		return switch (this) {
		case TITLE -> "medium_attribute.title";
		case AUTHORS -> "medium_attribute.authors";
		case EDITION -> "medium_attribute.edition";
		case PUBLISHER -> "medium_attribute.publisher";
		};
	}
}
