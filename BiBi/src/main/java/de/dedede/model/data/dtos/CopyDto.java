package de.dedede.model.data.dtos;

import java.util.Date;

import de.dedede.model.logic.util.AvailabilityStatus;

/**
 * This DTO (data transfer object) is responsible for aggregating and
 * encapsulating data about a copy of the medium for transfer.
 * <p>
 * See the {@link de.dedede.model.persistence.daos.MediumDao} class to which this DTO is passed.
 *
 * @author Sergei Pravdin
 */
public class CopyDto {

	private int id;

	private String location;

	private String signature;

	private Date deadline;

	//Wer hat das Copy entliehen / bemerkt hat
	private int actor;

	private AvailabilityStatus availabilityStatus;

	/**
	 * Fetches a physical location of the copy in a library.
	 *
	 * @return A location of the copy.
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * Sets a physical location of the copy in a library.
	 *
	 * @param location A location of the copy.
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * Fetches a signature of the copy.
	 *
	 * @return A signature of the copy.
	 */
	public String getSignature() {
		return signature;
	}

	/**
	 * Sets a signature of the copy.
	 *
	 * @param signature A signature if the copy.
	 */
	public void setSignature(String signature) {
		this.signature = signature;
	}

	/**
	 * Fetches a deadline of the copy. This deadline models the relationship between availability status and this copy.
	 * For example, a deadline date can be set after a copy has been reserved or after it has been loaned out.
	 *
	 * @return A deadline of the copy.
	 */
	public Date getDeadline() {
		return deadline;
	}

	/**
	 * Sets a deadline of the copy. This deadline models the relationship between availability status and this copy.
	 * For example, a deadline date can be set after a copy has been reserved or after it has been loaned out.
	 *
	 * @param deadline A deadline of the copy.
	 */
	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

	/**
	 * Fetches an availability status of the copy in a library.
	 *
	 * @return A availability status of the copy.
	 * @see AvailabilityStatus
	 */
	public AvailabilityStatus getAvailabilityStatus() {
		return availabilityStatus;
	}

	/**
	 * Sets an availability status of the copy in a library.
	 *
	 * @param availabilityStatus A availability status of the copy.
	 * @see AvailabilityStatus
	 */
	public void setAvailabilityStatus(AvailabilityStatus availabilityStatus) {
		this.availabilityStatus = availabilityStatus;
	}

	/**
	 * Fetches the id of the copy.
	 *
	 * @return An unique ID of the copy.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the id of the copy.
	 *
	 * @param id An unique ID of the copy.
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Fetches the the unique id of the user who performed the action on this copy.
	 * It modulates the dependency between the actor and the copy.
	 *
	 * @return An unique ID of the user who performed the action on this copy.
	 * @see UserDto
	 */
	public int getActor() {
		return actor;
	}

	/**
	 * Sets the the unique id of the user who performed the action on this copy.
	 * It modulates the dependency between the actor and the copy.
	 *
	 * @param actor An unique ID of the user who performed the action on this copy.
	 * @see UserDto
	 */
	public void setActor(int actor) {
		this.actor = actor;
	}
}
