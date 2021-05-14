package dedede.model.logic.managedbeans;

import java.util.List;

import dedede.model.data.dtos.CopyDto;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

/**
 * Backing bean for the facelet containg a list of copies ready for pickup for the current user.
 * On this page a user gets to know which copies they want to pick up from the library and
 * borrow thereafter and how much time they have left to do so until they exceed the lending period.
 */
@Named
@SessionScoped
public class MyCopiesReadyForPickUp extends PaginatedList {

	private List<CopyDto> listDto;

}
