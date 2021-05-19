package de.dedede.model.logic.managedbeans;

import java.io.Serializable;
import java.util.List;

import de.dedede.model.data.dtos.CopyDto;
import de.dedede.model.data.dtos.MediumDto;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.component.UIOutput;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

/**
 * Backing bean for the facelet containing the list of copies ready for pickup.
 * This page is used by library staff to be up to speed regarding which copies
 * are ready to be picked up and whether they can expect someone to soon enter
 * the library and arrive at their counter.
 *
 */
@Named
@RequestScoped
public class CopiesReadyForPickupAllUsers extends PaginatedList  {

	private List<CopyDto> copies;

	private List<MediumDto> list;



}
