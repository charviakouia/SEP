package dedede.model.data.dtos;

import java.util.Set;

/**
 * A class for aggregate and encapsulate data about a medium for transfer.
 * <p>
 * The class contains set CopyDTOs and AttributeDTOs.
 * See {@link CopyDto} and {@link AttributeDto} for more details about that.
 */
public class MediumDto {
	
	//private byte[] icon;
	
	
	
	/*private Date rückgabeFrist;
	
	private String verlag;
	
	private String auflage;
	
	private String freitext;
	
	private String title;
	
	private String type;
	
	private String publicationYear;
	
	private String index;
	
	private String mediumreference;*/
	
	private Integer id;
	
	private Set<AttributeDto> attribute;

	private Set<CopyDto> attribute;
	
	private String category;
	

}
