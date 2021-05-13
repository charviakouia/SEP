package dedede.model.logic.managedbeans;

import dedede.model.data.dtos.CopyDto;
import dedede.model.data.dtos.UserDto;

import java.util.List;

public class LendingPeriodViolation extends PaginatedList {
	
	private static class CopyWithUser {
	    private CopyDto copy;
	    private UserDto user;
	}
	private List<CopyWithUser> copiesWithUser;

}
