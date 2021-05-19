package de.dedede.model.data.dtos;

import java.util.Date;

import de.dedede.model.logic.util.AvailabilityStatus;

/**
 * A class for aggregate and encapsulate data about a medium's copy for
 * transfer.
 */
public class CopyDto {

	private int id;

	private String location;

	private String signature;

	//Was ist das? Das modelliert eine Abhängigkeit zw. availabilityStatus und einem Copy
	private Date deadline;

	//Wer hat das Copy entliehen / bemerkt hat
	private int actor;

	private AvailabilityStatus availabilityStatus;

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

	public AvailabilityStatus getAvailabilityStatus() {
		return availabilityStatus;
	}

	public void setAvailabilityStatus(AvailabilityStatus availabilityStatus) {
		this.availabilityStatus = availabilityStatus;
	}

}
