package de.dedede.model.logic.managed_beans;

import java.io.Serial;
import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;

import de.dedede.model.data.dtos.CopyDto;
import de.dedede.model.data.dtos.MediumCopyUserDto;
import de.dedede.model.data.dtos.MediumDto;
import de.dedede.model.data.dtos.PaginationDto;
import de.dedede.model.persistence.daos.MediumDao;
import de.dedede.model.persistence.exceptions.LostConnectionException;
import de.dedede.model.persistence.util.Logger;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.Flash;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Backing bean for the facelet containing the list of lending period violations. This page displays a list
 * of copies which haven’t been yet returned together with the user who exceeded the lending period.
 *
 * @author Ivan Charviakou
 */
@Named
@ViewScoped
public class LendingPeriodViolation extends PaginatedList implements Serializable {

	@Serial private static final long serialVersionUID = 1L;
	private static final int NUM_ROWS = 10;

	private List<MediumCopyUserDto> items;

	@PostConstruct
	public void init() {
		PaginationDto paginationDetails = new PaginationDto();
		paginationDetails.setPageNumber(0);
		paginationDetails.setTotalAmountOfRows(NUM_ROWS);
		setPaginatedList(paginationDetails);
		refresh();
	}

	@Override
	public void refresh() {
		try {
			updateNumberOfPages();
			items = MediumDao.readAllOverdueCopies(getPaginatedList());
		} catch (LostConnectionException e){
			String msg = "Couldn't update list of entries";
			Logger.severe(msg);
		}
	}

	private void updateNumberOfPages(){
		PaginationDto paginationDetails = getPaginatedList();
		int numRowsPerPage = paginationDetails.getTotalAmountOfRows();
		int numRows = MediumDao.readNumberOfAllOverdueCopies();
		long numPages = Math.round(Math.floor(numRows / (double) numRowsPerPage)) + 1;
		paginationDetails.setTotalAmountOfPages(Math.toIntExact(numPages));
		setPaginatedList(paginationDetails);
	}

	@Override
	public List<MediumCopyUserDto> getItems() {
		return items;
	}

	/**
	 * Returns a navigation link to the medium page for the passed data entry.
	 *
	 * @param dto The passed {@link MediumCopyUserDto} data entry
	 * @return A navigation link for the associated medium DTO
	 * @see MediumDto
	 */
	public String getMediumLink(MediumCopyUserDto dto) {
		if (dto.getMedium() == null) {
			return "/view/opac/medium.xhtml?faces-redirect=true";
		} else {
			return "/view/opac/medium.xhtml?faces-redirect=true&" + "id=" + dto.getMedium().getId();
		}
	}

	/**
	 * Returns the title of the medium for the passed data entry. If the entry contains null in place
	 * of a medium, the String '---' is returned instead.
	 *
	 * @param dto The passed {@link MediumCopyUserDto} data entry
	 * @return The title of the associated medium DTO
	 * @see MediumDto
	 */
	public String getMediumLabel(MediumCopyUserDto dto) {
		if (dto.getMedium() == null) {
			return "---";
		} else {
			return dto.getMedium().getTitle();
		}
	}

	/**
	 * Executes a navigation link to the medium-copy return page for the passed data entry. Specifically,
	 * the page's user-email and medium-copy signature fields are filled with data from the medium-copy.
	 *
	 * @param dto The passed {@link MediumCopyUserDto} data entry
	 * @return The navigation link to the return page for the associated medium-copy
	 * @see CopyDto
	 */
	public String executeCopyLink(MediumCopyUserDto dto){
		Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
		flash.put("userId", dto.getUser().getId());
		flash.put("copySignature", dto.getCopy().getSignature());
		return "/view/staff/return-form.xhtml?faces-redirect=true";
	}

	/**
	 * Returns the signature of the medium-copy for the passed data entry. If the entry contains null in place
	 * of a medium-copy, the String '---' is returned instead.
	 *
	 * @param dto The passed {@link MediumCopyUserDto} data entry
	 * @return The signature of the associated medium-copy DTO
	 * @see CopyDto
	 */
	public String getCopyLabel(MediumCopyUserDto dto) {
		if (dto.getCopy() == null) {
			return "---";
		} else {
			return dto.getCopy().getSignature();
		}
	}

	/**
	 * Returns a navigation link to the user-profile page for the passed data entry.
	 *
	 * @param dto The passed {@link MediumCopyUserDto} data entry
	 * @return The navigation link to the user-profile page for the associated user
	 */
	public String getUserLink(MediumCopyUserDto dto) {
		if (dto.getUser() == null) {
			return "/view/account/profile.xhtml?faces-redirect=true";
		} else {
			return "/view/account/profile.xhtml?faces-redirect=true&" + "id=" + dto.getUser().getId();
		}
	}

	/**
	 * Returns the email-address of the user associated with the passed data entry. If the entry contains null
	 * in place of a user, the String '---' is returned instead.
	 *
	 * @param dto The passed {@link MediumCopyUserDto} data entry
	 * @return The email-address of the associated user
	 */
	public String getUserLabel(MediumCopyUserDto dto) {
		if (dto.getUser() == null) {
			return "---";
		} else {
			return dto.getUser().getEmailAddress();
		}
	}

	/**
	 * Returns the amount of time that a medium-copy, as represented by the passed data entry, has been
	 * overdrawn. If the entry contains null in place of a duration-value, the String '---' is returned instead.
	 *
	 * @param dto The passed {@link MediumCopyUserDto} data entry
	 * @return Amount of time by which the given medium-copy has been overdrawn, formatted as: 'DAYS, HOURS:MINUTES'
	 */
	public String getOverdraftText(MediumCopyUserDto dto) {
		if (dto.getOverdraft() == null) {
			return "---";
		} else {
			Duration duration = dto.getOverdraft();
			long seconds = Math.toIntExact(duration.getSeconds());
			return String.format("%02d, %02d:%02d", seconds / (24 * 60 * 60), 
					(seconds % (24 * 60 * 60)) / (60 * 60), 
					(seconds % (60 * 60)) / 60);
		}
	}
	
}


