package dedede.model.logic.managedbeans;

import java.io.Serializable;
import java.util.List;

import dedede.model.data.dtos.CopyDto;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

/**
 * Backing bean for the page containing a list of borrowed copies of the current user.
 * This page informs the user which copies they borrowed from the library and how much
 * time they still have left until they have to return them to not exceed the lending period.
 */
@Named
@SessionScoped
public class MyBorrwedCopies extends PaginatedList implements Serializable {

	private List<CopyDto> listDto;

}
