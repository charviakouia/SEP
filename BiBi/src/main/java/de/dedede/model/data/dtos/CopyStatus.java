package de.dedede.model.data.dtos;

/**
 * Represents the the status of a copy.
 */
public enum CopyStatus {

	BORROWED, READY_FOR_PICKUP, AVAILABLE;
	
	@Override
	public String toString() {
		return switch(this) {
		case BORROWED -> "copy_status.borrowed";
		case READY_FOR_PICKUP -> "copy_status.ready_for_pickup";
		case AVAILABLE -> "copy_status.available";
		};
	}

}
