package de.dedede.model.logic.managed_beans;

import java.io.Serial;
import java.util.List;

import de.dedede.model.data.dtos.MediumCopyAttributeUserDto;
import de.dedede.model.data.dtos.PaginationDto;
import de.dedede.model.persistence.daos.MediumDao;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.inject.Inject;
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
public class CopiesReadyForPickupAllUsers extends PaginatedList {

	@Inject
	private ExternalContext ectx;
	
	@Serial
	private List<MediumCopyAttributeUserDto> copies;

	@PostConstruct
	public void init() {
		copies = MediumDao.readCopiesReadyForPickup(new PaginationDto());
	}
	
	public String getAttributeName() {
		// the name is the same for all copies
		return copies.get(0).getAttribute().getName();
	}
	
	public String goToLending(String signature) {
		ectx.getFlash().put("signature", signature);
		
		return "lending?faces-redirect=true";
	}

	@Override
	public List<MediumCopyAttributeUserDto> getItems() {
		return copies;
	}
}
