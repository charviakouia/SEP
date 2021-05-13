package dedede.model.logic.managedbeans;

import java.util.List;

import dedede.model.data.dtos.CopyDto;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

/**
 * Backing bean for the facelet containing the list of copies ready for pickup.
 * This page is used by library staff to be up to speed regarding which copies are ready to
 * be picked up and whether they can expect someone to soon enter the library and arrive at their
 * counter.
 *
 */
@Named
@ViewScoped
public class CopiesReadyForPickup extends PaginatedList {

	private List<CopyDto> listCopies;

}
