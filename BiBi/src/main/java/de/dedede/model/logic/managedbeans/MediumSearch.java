package de.dedede.model.logic.managedbeans;

import java.io.Serializable;
import java.util.ArrayList;

import de.dedede.model.logic.util.AttributeOrCategory;
import de.dedede.model.logic.util.SearchOperator;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

/**
 * Backing bean for the medium search page. This page permits users to search
 * for mediums given some of their attributes as search criteria. Further, a
 * user can search mediums by category and by the signature of one of their
 * copies. The page has an optional query parameter corresponding to the search
 * term. If it is not provided, nothing will be searched and the paginated list
 * of results will not be displayed at all.
 */
@Named
@ViewScoped
public class MediumSearch extends PaginatedList implements Serializable {

	private String generalSearchTerm;

	private ArrayList<NuancedSearchQuery> nuancedSearchQueries;

	/**
	 * Search for the corresp. mediums.
	 */
	public void search() {

	}

	/**
	 * Add a nuanced seatch query which is the grouping of an operator, a criterion
	 * and a search term.
	 */
	public void addNuancedSearchQuery() {

	}

}

class NuancedSearchQuery {

	private SearchOperator operator;

	private AttributeOrCategory criterion;

	private String searchTerm;
}
