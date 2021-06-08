package de.dedede.model.logic.managed_beans;

import java.io.Serial;
import java.util.List;

import de.dedede.model.data.dtos.MediumCopyAttributeUserDto;
import de.dedede.model.data.dtos.PaginationDto;
import de.dedede.model.persistence.daos.MediumDao;
import de.dedede.model.persistence.exceptions.LostConnectionException;
import de.dedede.model.persistence.exceptions.MaxConnectionsException;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
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

	@Serial
	private List<MediumCopyAttributeUserDto> copies;

	@PostConstruct
	public void init() {
		try {
			copies = MediumDao.readCopiesReadyForPickup(new PaginationDto());
		} catch (LostConnectionException | MaxConnectionsException e) {
			// @Task enhance
			throw new RuntimeException("database connection issue");
		}

	}

	@Override
	public List<MediumCopyAttributeUserDto> getItems() {
		return copies;
	}
}
