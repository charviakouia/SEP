package dedede.model.logic.util;

import jakarta.faces.event.PhaseEvent;
import jakarta.faces.event.PhaseId;
import jakarta.faces.event.PhaseListener;

/**
 * Checks user requests based on loginstatus, {@link Role} and the requested page of the
 * application. It denies access to pages where the {@link dedede.model.data.dtos.UserDto} has no rights for. Uses
 * the session map to check the loginstatus of the user. Checks the user role by checking the actual
 * class of the user ({@link dedede.model.data.dtos.UserDto}, NormalUser, Admin, LibraryStaff.
 * The user roles are structured as in the following: NormalUser - LibraryStaff - Admin.
 * The user role  can access all
 * pages of them. If a lower role navigates to a page, where he has no rights for a message appears
 * that this page does not exists.
 */
public class TrespassListener implements PhaseListener {

	/**
	 * The processes the TrespassListener performs before the phase is executed. In this case the
	 * method is empty.
	 *
	 * @param phaseEvent The event on which the listener is triggered.
	 */
	@Override
	public void beforePhase(PhaseEvent phaseEvent) { }

	/**
	 * Returns the phase in which the TrespassListener gets active.
	 *
	 * @return the phase where the listener gets active.
	 */
	@Override
	public PhaseId getPhaseId() {
		return null;
	}

	/**
	 * The processes the TrespassListener performs after the phase is executed.
	 *
	 * @param phaseEvent The event on which the listener is triggered.
	 */
	@Override
	public void afterPhase(PhaseEvent phaseEvent) {}





}
