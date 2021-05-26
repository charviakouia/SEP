package de.dedede.model.logic.managedbeans;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import de.dedede.model.data.dtos.CopyMediumUser;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

/**
 * Backing bean for the facelet containing the list of lending period
 * violations. This page displays a list of copies which haven’t been returned
 * yet together with the associated user who exceeded the lending period.
 */
@Named
@SessionScoped
public class LendingPeriodViolation extends PaginatedList implements Serializable {

	@Serial
	private static  final long serialVersionUID = 1L;

	private List<CopyMediumUser> items;

	@PostConstruct
	public void init() {

	}
	
	@Override
	public List<CopyMediumUser> getItems() {
		return items;
	}
}


