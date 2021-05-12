package dao;

import java.util.List;

import dto.CopyDto;
import dto.MediumDto;
import dto.UserDto;



public final class MediumDao {
	
	
	
	public static void createMedium( MediumDto mediumDto) {
		
		
	}
	
	public static void updateMedium( MediumDto mediumDto) {
		
		
	}
	
	public static void deleteMedium( MediumDto mediumDto) {
		
		
	}
	
	public static MediumDto   loadMedium( ) {
		
		return null;
	}
	
	
	public static List<MediumDto> getAllMediums(){
		return null;
		
	}
	
   public static List<MediumDto> searchMediums(String text){
	return null;
		
	}
	
	//Exemplare
   
   public static void updateCopy(CopyDto copyDto) {
		
		
	}
	
   public static void deleteCopy (CopyDto copyDto) {
		
		
	}
   
   public static void createCopy (CopyDto copyDto) {
		
		
   	
	}
   public static  CopyDto loadCopy (String signatureLocation) {
   	
		return null;
		
		
	}
  
   
   public static  List<CopyDto>  getAllOverdueCopies(){
   	return null;
   }
   
   
   public static List<CopyDto>  getAllCopiesByMediums(MediumDto mediumDto){
   	return null;
   }
   
   public static void lendCopy ( CopyDto copyDto, UserDto userDto) {
   	
   	
   	
   }
   
   public static void returnCopy ( CopyDto copyDto, UserDto userDto) {
   	
   	
   	
   }
   

   
   
}
