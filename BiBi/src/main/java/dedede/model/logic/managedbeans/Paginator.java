package dedede.model.logic.managedbeans;

import java.util.List;

public class Paginator<T> {
	
	private List<T> list;
	
	private int sizeOfPage;
	
	private String sortBy;
	
	
	
	
	
	public Paginator() {
		
	}
	// Der Constructor
	public List<T> getPage(int pageNumber) {
		return null;
	} 
	
	// oder durch eine statische Methode
	public static List<Object> getPageFromList(List<Object> list, int sizeOfPage, int pageNumber) {
		return null;
	} 
} 
