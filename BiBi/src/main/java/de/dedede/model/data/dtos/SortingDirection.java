package de.dedede.model.data.dtos;

/**
 * @author León Liehr
 */
public enum SortingDirection {
	ASCENDING,
	DESCENDING;
	
	public String symbol() {
		return switch(this) {
		case ASCENDING -> "↑";
		case DESCENDING -> "↓";
		};
	}
	
	public SortingDirection inverted() {
		return switch (this) {
		case ASCENDING -> DESCENDING;
		case DESCENDING -> ASCENDING;
		};
	}
}
