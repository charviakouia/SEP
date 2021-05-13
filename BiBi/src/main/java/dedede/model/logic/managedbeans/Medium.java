package dedede.model.logic.managedbeans;

import dedede.model.data.dtos.CopyDto;
import dedede.model.data.dtos.MediumDto;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

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
