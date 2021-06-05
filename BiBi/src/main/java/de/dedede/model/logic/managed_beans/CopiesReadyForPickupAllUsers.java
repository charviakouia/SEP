package de.dedede.model.logic.managed_beans;

import java.io.Serial;
import java.util.List;

import de.dedede.model.data.dtos.CopyDto;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

/**
 * Backing bean for the facelet containing the list of copies ready for pickup.
 * This page is used by library staff to be up to speed regarding which copies
 * are ready to be picked up and whether they can expect someone to soon enter
 * the library and arrive at their counter.
 *
 */
@Named
@RequestScoped
public class CopiesReadyForPickupAllUsers extends PaginatedList {

	@Serial
	private List<CopyDto> copies;

	@PostConstruct
	public void init() {

	}

	@Override
	public List<CopyDto> getItems() {
		return copies;
	}
	
	public List<Pair> getStuff() {
		return List.of(new Pair("alp", "ha"), new Pair("gam", "ma"));
	}
	
	public static class Pair {
		
		private String first;
		
		private String second;

		public Pair(String first, String second) {
			super();
			this.first = first;
			this.second = second;
		}

		public String getFirst() {
			return first;
		}

		public void setFirst(String first) {
			this.first = first;
		}

		public String getSecond() {
			return second;
		}

		public void setSecond(String second) {
			this.second = second;
		}
		
	}

}
