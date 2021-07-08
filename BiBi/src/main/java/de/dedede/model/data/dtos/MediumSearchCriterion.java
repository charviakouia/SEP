package de.dedede.model.data.dtos;

/**
 * A medium search criterion.
 * The criteria mostly correspond to the attributes of a medium except for {@link AUTHORS} which combines {@code author1}, …, {@code author5}
 * and {@link SIGNATURE} which is copy-specific, not medium-specific.
 * 
 * @author León Liehr
 */
public enum MediumSearchCriterion {

	TITLE, AUTHORS, TYPE, EDITION, PUBLISHER, YEAR_OF_RELEASE, ISBN, URL, SUMMARY, CATEGORY, SIGNATURE;

	/**
	 * Indicates whether this criterion is used in the general search i.e. if this criterion should be used in the lowering/
	 * desugaring/decomposition from/of the general search query into several nuanced search queries.
	 * 
	 * @return The fact if this criterion is used for the general search.
	 */
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
