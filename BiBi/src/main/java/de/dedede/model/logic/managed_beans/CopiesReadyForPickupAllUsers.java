package de.dedede.model.logic.managed_beans;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import de.dedede.model.data.dtos.MediumCopyUserDto;
import de.dedede.model.persistence.daos.MediumDao;
import jakarta.annotation.PostConstruct;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.view.ViewScoped;
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
@ViewScoped
public class CopiesReadyForPickupAllUsers extends PaginatedList implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Inject
	private ExternalContext ectx;
	
	private List<MediumCopyUserDto> copies;

	@PostConstruct
	public void init() {
		fetchData();
	}
	
	public String goToLending(String signature) {
		ectx.getFlash().put("signature", signature);
		
		return "lending?faces-redirect=true";
	}

	@Override
	public List<MediumCopyUserDto> getItems() {
		return copies;
	}

	@Override
	public void refresh() {
		fetchData();
	}
	
	private void fetchData() {
		copies = MediumDao.readCopiesReadyForPickup(getPaginatedList());
	}
}
