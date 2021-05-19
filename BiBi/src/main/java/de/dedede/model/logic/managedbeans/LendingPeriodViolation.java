package de.dedede.model.logic.managedbeans;

import java.io.Serializable;
import java.util.List;

import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.data.dtos.CopyDto;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

/**
 * Backing bean for the facelet containing the list of lending period
 * violations. This page displays a list of copies which haven’t been returned
 * yet together with the assoc. user who exceeded the lending period.
 */
@Named
@SessionScoped
public class LendingPeriodViolation extends PaginatedList implements Serializable {

	private List<CopyWithUser> copiesWithUser;

}

class CopyWithUser {
	private CopyDto copy;
	private UserDto user;
}
