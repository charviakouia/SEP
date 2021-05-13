package dedede.model.logic.managedbeans;

import java.util.List;

import dedede.model.data.dtos.CopyDto;
import dedede.model.data.dtos.UserDto;

public class LendingPeriodViolation extends PaginatedList {

	private static class CopyWithUser {
		private CopyDto copy;
		private UserDto user;
	}

	private List<CopyWithUser> copiesWithUser;

}
