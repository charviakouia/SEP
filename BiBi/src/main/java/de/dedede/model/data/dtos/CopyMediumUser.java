package de.dedede.model.data.dtos;

public class CopyMediumUser {

	private CopyDto copy;

	private MediumDto medium;

	private UserDto user;

	public CopyDto getCopy() {
		return copy;
	}

	public void setCopy(CopyDto copy) {
		this.copy = copy;
	}

	public MediumDto getMedium() {
		return medium;
	}

	public void setMedium(MediumDto medium) {
		this.medium = medium;
	}

	public UserDto getUser() {
		return user;
	}

	public void setUser(UserDto user) {
		this.user = user;
	}
}
