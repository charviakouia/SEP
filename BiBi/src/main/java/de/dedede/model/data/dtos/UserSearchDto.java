package de.dedede.model.data.dtos;

public class UserSearchDto {

	private String searchTerm;

	private UserLendStatus lendStatus;
	
	private UserRole role;

	public String getSearchTerm() {
		return searchTerm;
	}

	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}

	public UserLendStatus getLendStatus() {
		return lendStatus;
	}

	public void setLendStatus(UserLendStatus userStatus) {
		this.lendStatus = userStatus;
	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}
	
}
