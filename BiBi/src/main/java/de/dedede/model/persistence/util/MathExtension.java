package de.dedede.model.persistence.util;

/**
 * A collection of methods performing basic numeric operations missing from
 * {@link java.lang.Math}.
 * 
 * @author León Liehr
 */
public final class MathExtension {

	private MathExtension() {
		throw new IllegalStateException("Attempt to construct the utility class MathExtension");
	}

	/**
	 * Integer division rounding up to the next integer.
	 * 
	 * The standard integer division rounds down, consider {@code 7 / 2 == 3}. This
	 * method on the other hand results in {@code 4} for the arguments {@code 7} and
	 * {@code 2}.
	 * 
	 * @param divident The divident.
	 * @param divisor  The divisor.
	 * @return The rounded-up quotient of the division.
	 */
	public static int ceilDiv(int divident, int divisor) {
		return 1 + ((divident - 1) / divisor);
	}
}
