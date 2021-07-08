package de.dedede.model.data.dtos;

import java.sql.Timestamp;
import java.time.Duration;

/**
 * This DTO (data transfer object) aggregates the DTOs {@link CopyDto}, {@link MediumDto} and {@link UserDto}.
 * 
 * @author León Liehr
 */
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

	// <author: Ivan Charviakou>
	
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
	
	// </author: Ivan Charviakou>
}
