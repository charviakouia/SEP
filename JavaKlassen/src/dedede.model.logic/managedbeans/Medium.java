package dedede.model.logic.managedbeans;

import javax.annotation.PostConstruct;

import dto.CopyDto;
import dto.MediumDto;
@Named
@ViewScoped
public class Medium extends PaginatedList{
	
	private MediumDto medium;
	
	private CopyDto copyDto;
	
	@PostConstruct
	public  void init() {
		
	}
	public void update() {
		
	}
	
	public void updateReturnPeriod() {
		
	}
	public void createCopy() {
		
	}
	
	public void pickUpAnyCopy() {
		
	}
	
	public void deleteCopy() {
		
	}
	
	public void editCopy() {
		
	}
	
	public void cancelLending() {
		
	}
	
	public void lendDirectly() {
		
	}
	

}
