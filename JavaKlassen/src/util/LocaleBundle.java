package util;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Named
@SessionScoped
public class LocaleBundle implements Serializable {
	
	private Locale local;
	
	private DateTimeFormatter dateTimeFormatter;
}
