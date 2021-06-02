package de.dedede.model.logic.managed_beans;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import de.dedede.model.data.dtos.AttributeDto;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

/**
 * Backing bean for the medium schema editor page.
 */
@Named
@SessionScoped
public class MediumSchemaEditor implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	private final Map<Integer, AttributeDto> attributes = new HashMap<Integer, AttributeDto>();

	/**
	 * Delete a medium attribute.
	 * 
	 * @param id The identifier of the attribute to be deleted.
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
