package dedede.model.logic.managedbeans;

import java.util.List;

import dto.CopyDto;
import dto.UserDto;

public class LendingPeriodViolation extends PaginatedList {
	
	private static class CopyWithUser {
	    private CopyDto copy;
	    private UserDto user;
	}
	private List<CopyWithUser> copiesWithUser;

}
