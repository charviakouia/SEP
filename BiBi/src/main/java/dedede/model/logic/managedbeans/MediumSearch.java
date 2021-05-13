package dedede.model.logic.managedbeans;

import java.util.ArrayList;

import dedede.model.logic.util.AttributeOrCategory;
import dedede.model.logic.util.SearchOperator;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

@Named
@ViewScoped
public class MediumSearch extends PaginatedList {
	
	private String generalSearchTerm;
	
	private ArrayList<NuancedSearchQuery> nuancedSearchQueries;
	
	public void search() {
		
	}
	
	public void addSearchField() {
		
	}

}

class NuancedSearchQuery {
	
	private SearchOperator operator;
	
	private AttributeOrCategory criterion;
	
	private String searchTerm;
}
