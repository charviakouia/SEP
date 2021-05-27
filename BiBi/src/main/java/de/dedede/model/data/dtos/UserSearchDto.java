package de.dedede.model.data.dtos;

public class UserSearchDto {

	private String searchTerm;

	private UserLendStatus userLendStatus;

	public String getSearchTerm() {
		return searchTerm;
	}

	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}

	public UserLendStatus getUserLendStatus() {
		return userLendStatus;
	}

	public void setUserLendStatus(UserLendStatus userLendStatus) {
		this.userLendStatus = userLendStatus;
	}
	
	
}
