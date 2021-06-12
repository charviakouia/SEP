package de.dedede.model.logic.util;

/**
 * Represents for the the verification status. It could be verified or
 * unverified.
 */
public enum UserVerificationStatus {
	VERIFIED, UNVERIFIED;

	@Override
	public String toString() {
		return switch(this) {
			case VERIFIED -> "user_status.verified";
			case UNVERIFIED -> "user_status.unverified";
		};
	}
}
