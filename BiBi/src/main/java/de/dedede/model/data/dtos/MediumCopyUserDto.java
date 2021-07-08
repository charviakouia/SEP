package de.dedede.model.data.dtos;

import java.sql.Timestamp;
import java.time.Duration;

/**
 * The DTO used to supply the {@link de.dedede.model.logic.managed_beans.LendingPeriodViolation} facelet with data.
 * An instance of this DTO represents a data entry composed of a medium-copy, copy, user, and overdraft. Here, the
 * given user has exceeded the allowed lending-duration of a medium-copy belonging to a medium by a given overdraft
 * period.
 *
 * @author Ivan Charviakou
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
