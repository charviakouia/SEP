package de.dedede.model.logic.managed_beans;

import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import de.dedede.model.data.dtos.MediumCopyUserDto;
import de.dedede.model.data.dtos.PaginationDto;
import de.dedede.model.persistence.daos.MediumDao;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

/**
 * Backing bean for the facelet containing the list of lending period
 * violations. This page displays a list of copies which haven’t been returned
 * yet together with the associated user who exceeded the lending period.
 */
@Named
@SessionScoped
public class LendingPeriodViolation extends PaginatedList implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;
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
	public List<MediumCopyUserDto> getItems() {
		return items;
	}

	@Override
	public void refresh() {
		PaginationDto paginationDetails = getPaginatedList();
		int numRowsPerPage = paginationDetails.getTotalAmountOfRows();
		long numPages = Math.round(Math.floor(MediumDao.readNumberOfAllOverdueCopies() / (double) numRowsPerPage));
		paginationDetails.setTotalAmountOfPages(Math.toIntExact(numPages) + 1);
		items = MediumDao.readAllOverdueCopies(paginationDetails);
		setPaginatedList(paginationDetails);
	}
	
	public String getMediumLink(MediumCopyUserDto dto) {
		if (dto.getMedium() == null) {
			return "/view/public/medium.xhtml?faces-redirect=true";
		} else {
			return "/view/public/medium.xhtml?faces-redirect=true&" + "id=" + dto.getMedium().getId();
		}
	}
	
	public String getMediumLabel(MediumCopyUserDto dto) {
		if (dto.getMedium() == null) {
			return "---";
		} else {
			return dto.getMedium().getTitle();
		}
	}
	
	public String getCopyLink(MediumCopyUserDto dto) {
		return "/view/staff/return-form.xhtml?faces-redirect=true";
	}
	
	public String getCopyLabel(MediumCopyUserDto dto) {
		if (dto.getCopy() == null) {
			return "---";
		} else {
			return dto.getCopy().getSignature();
		}
	}

	public String getUserLink(MediumCopyUserDto dto) {
		if (dto.getUser() == null) {
			return "/view/account/profile.xhtml?faces-redirect=true";
		} else {
			return "/view/account/profile.xhtml?faces-redirect=true&" + "id=" + dto.getUser().getId();
		}
	}
	
	public String getUserLabel(MediumCopyUserDto dto) {
		if (dto.getUser() == null) {
			return "---";
		} else {
			return dto.getUser().getEmailAddress();
		}
	}
	
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


