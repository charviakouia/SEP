package de.dedede.model.data.dtos;

import java.util.Date;

import de.dedede.model.logic.util.CopyStatus;

/**
 * A class for aggregate and encapsulate data about a medium's copy for
 * transfer.
 */
public class CopyDto {

	private String location;

	private String signature;

	private Date deadline;

	private CopyStatus copyStatus;

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public Date getDeadline() {
		return deadline;
	}

	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

	public CopyStatus getAvailabilityStatus() {
		return copyStatus;
	}

	public void setAvailabilityStatus(CopyStatus copyStatus) {
		this.copyStatus = copyStatus;
	}

}
