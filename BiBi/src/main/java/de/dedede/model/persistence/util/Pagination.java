package de.dedede.model.persistence.util;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.dedede.model.data.dtos.PaginationDto;
import de.dedede.model.data.dtos.SortingDirection;

/**
 * Aggregate of common operations on paginated lists or more specifically on
 * {@link PaginationDto}s.
 * 
 * @author León Liehr
 */
public final class Pagination {

	private Pagination() {
		throw new IllegalStateException("Attempt to construct the utility class Pagination");
	}

	/**
	 * Get the number of entries per page of a paginated list.
	 * 
	 * @return The number of entries per page.
	 */
	public static int getEntriesPerPage() {
		return ConfigReader.getInstance().getKeyAsInt("MAX_PAGES");
	}

	/**
	 * Update the current page number and the total amount of pages of given
	 * {@link PaginationDto pagination details}.
	 * 
	 * @param pagination The pagination details to be updated.
	 * @param pageCount  The amount of pages as fetched from the database.
	 */
	public static void update(PaginationDto<?> pagination, int pageCount) {
		pagination.setTotalAmountOfPages(ceilDiv(pageCount, getEntriesPerPage()));
		// in general not entirely "correct"
		pagination.setPageNumber(Math.min(pagination.getPageNumber(), pagination.getTotalAmountOfPages() - 1));
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
	private static int ceilDiv(int divident, int divisor) {
		return 1 + ((divident - 1) / divisor);
	}

	/**
	 * Translate the page number to an offset usable inside of an SQL query.
	 * 
	 * @param paginationDto The container with the page number.
	 * @return An offset into the data table.
	 */
	public static int calculatePageOffset(PaginationDto<?> pagination) {
		return pagination.getPageNumber() * getEntriesPerPage();
	}

	/**
	 * Translate the sorting information contained within the {@link PaginationDto
	 * pagination details} to an SQL order-by clause.
	 * 
	 * This method takes a function as a parameter, the "serializer", to allow the
	 * caller to define the mapping from the given structured column enum type to
	 * the corresponding SQL column name (or even path). Important: Do NOT
	 * incorporate data supplied by users in the serializer to prevent possible SQL
	 * injection attacks!
	 * 
	 * The reason why the serialization to SQL is separate from the column enum type
	 * is the preservation of the layer model. It could have been modeled by an
	 * interface with a stringification method but then the actual names of the SQL
	 * columns would leak out of the persistence layer into the model layer where
	 * the DTOs or more specifically the column enum types are defined. Apart from
	 * that, it is more flexible allowing the caller to freely use "SQL name
	 * bindings" (e.g. binding table `alpha` to `a` and then referencing the column
	 * `a.col`).
	 * 
	 * @param <Column>   The column enum type of the pagination details.
	 * @param pagination The pagination details with the sorting information.
	 * @param serializer The serializer for turning enum values into SQL column
	 *                   names.
	 * @return An order-by clause corresponding to the given sorting information.
	 */
	public static <Column extends Enum<Column>> String translateSortingInfoToSQL(PaginationDto<Column> pagination,
			Function<Column, String> serializer) {
		if (pagination.getColumnToSortBy() == null) {
			return "";
		} else {
			return "order by %s %s".formatted(serializer.apply(pagination.getColumnToSortBy()),
					translateSortingDirectionToSQL(pagination.getSortingDirection()));
		}
	}

	/**
	 * Translate the sorting information contained within the {@link PaginationDto
	 * pagination details} to an SQL order-by clause.
	 * 
	 * This method takes a function as a parameter, the "serializer", to allow the
	 * caller to define the mapping from the given structured column enum type to a
	 * corresponding list of SQL column names (or even paths). Important: Do NOT
	 * incorporate data supplied by users in the serializer to prevent possible SQL
	 * injection attacks! This is useful for mapping "column aggregates" modeling
	 * multi-valued attributes etc. to concrete single-values SQL columns.
	 * 
	 * The reason why the serialization to SQL is separate from the column enum type
	 * is the preservation of the layer model. It could have been modeled by an
	 * interface with a stringification method but then the actual names of the SQL
	 * columns would leak out of the persistence layer into the model layer where
	 * the DTOs or more specifically the column enum types are defined. Apart from
	 * that, it is more flexible allowing the caller to freely use "SQL name
	 * bindings" (e.g. binding table `alpha` to `a` and then referencing the column
	 * `a.col`).
	 * 
	 * @param <Column>   The column enum type of the pagination details.
	 * @param pagination The pagination details with the sorting information.
	 * @param serializer The serializer for turning enum values into SQL column
	 *                   names.
	 * @return An order-by clause corresponding to the given sorting information.
	 */
	public static <Column extends Enum<Column>> String translateSortingInfoToSQLMultiValued(
			PaginationDto<Column> pagination, Function<Column, List<String>> serializer) {
		if (pagination.getColumnToSortBy() == null) {
			return "";
		} else {
			final var direction = translateSortingDirectionToSQL(pagination.getSortingDirection());

			return "order by %s".formatted(serializer.apply(pagination.getColumnToSortBy()).stream()
					.map(column -> "%s %s".formatted(column, direction)).collect(Collectors.joining(",")));
		}
	}

	private static String translateSortingDirectionToSQL(SortingDirection direction) {
		return switch (direction) {
		case ASCENDING -> "asc";
		case DESCENDING -> "desc";
		};
	}

}
