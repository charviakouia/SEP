package de.dedede.model.data.dtos;

/**
 * Represents a search operator.
 */
public enum SearchOperator {

	AND, OR, AND_NOT;

	@Override
	public String toString() {
		return switch (this) {
		case AND -> "search_operator.and";
		case OR -> "search_operator.or";
		case AND_NOT -> "search_operator.and_not";
		};
	}
}
