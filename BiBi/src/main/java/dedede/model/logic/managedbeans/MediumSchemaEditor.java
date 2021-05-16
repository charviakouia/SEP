package dedede.model.logic.managedbeans;

import java.io.Serializable;
import java.util.Set;

import dedede.model.data.dtos.AttributeDto;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

/**
 * Backing bean for the medium schema editor page.
 */
@Named
@SessionScoped
public class MediumSchemaEditor implements Serializable {

	private Set<AttributeDto> attributes;
	
	/**
	 * Delete a medium attribute.
	 * 
	 * @param id The identifier of the attributte to be deleted.
	 * @throws IllegalArgumentException If the identifier is invalid.
	 */
	public void deleteAttribute(int id) throws IllegalArgumentException {
		
	}
	
	
	/**
	 * Add an input field for an attribute.
	 */
    public void addAttributeInputField() {
		
	}
    
    /**
     * Save the changes made to the medium schema.
     */
    public void save() {
    	
    }
    
    
}
