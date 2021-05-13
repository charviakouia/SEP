package dedede.model.persistence.daos;

import dedede.model.data.dtos.CategoryDto;
import dedede.model.logic.managedbeans.Page;


public final class CategoryDao {
	
	private UserDao userService;

	
	private CategoryDao categoryDao;
	
	
	
	public static void createCategory(CategoryDto categoryDto) {
		
		
	}
    public static void updateCategory(CategoryDto categoryDto) {
    	
		
	}
    public static void deleteCategory(CategoryDto categoryDto) {
    	
    	
    }
 
    public static CategoryDto loadCategory() {
	 
	   return null;
		
	}
    
    public static Page<CategoryDto> getAllCategories(int pageSize, int pageNumber){
	  
    	return null;
	     
   }
    
    public static Page<CategoryDto> searchCategory ( String text, int pageSize, int pageNmber){
	 
    	return null;
    	
    
    }
}
