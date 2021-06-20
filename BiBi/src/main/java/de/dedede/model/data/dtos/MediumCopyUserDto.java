package de.dedede.model.data.dtos;

import java.sql.Timestamp;
import java.time.Duration;

public class MediumCopyUserDto {

	private CopyDto copy;
	
	private MediumDto medium;

	private UserDto user;
	
	private Duration lendingDuration;

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

	public Duration getOverdraft() {
		if (getCopy() == null || getCopy().getDeadline() == null) {
			return null;
		} else {
			Timestamp deadline = getCopy().getDeadline();
			long diff = System.currentTimeMillis() - deadline.getTime();
			return Duration.ofMillis(diff);
		}
	}

	public Duration getLendingDuration() {
		return lendingDuration;
	}

	public void setLendingDuration(Duration lendingDuration) {
		this.lendingDuration = lendingDuration;
	}
	
}
