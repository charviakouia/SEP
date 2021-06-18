package de.dedede.model.logic.converters;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

import jakarta.faces.application.Application;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.convert.FacesConverter;

/**
 * Converts the textual input into java.time.Duration and vice versa.
 * Modelled after @see DurationConverter.
 * 
 * @author Jonas Picker
 */
@FacesConverter("hoursToDurationConverter")
public class HoursToDurationConverter implements Converter<Duration> {

	@Override
	public Duration getAsObject(FacesContext context, UIComponent component, 
			String input) {
		Application application = context.getApplication();
		ResourceBundle messages = application.evaluateExpressionGet(context, 
				"#{msg}", ResourceBundle.class);
		try {
            int numberOfHours = Integer.parseInt(input);
            if (numberOfHours < 0) {
            	String shortMessage = messages.getString("administration.no"
            			+ "_negatives_short");
            	String longMessage = messages.getString("administration.no"
            			+ "_negatives_long");
            	FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
            			shortMessage, longMessage);
            	throw new ConverterException(msg);
            } else {
            	return Duration.of(Integer.toUnsignedLong(numberOfHours),
            			ChronoUnit.HOURS);
            }
        } catch (NumberFormatException e){
        	String shortMessage = messages.getString("administration.invalid"
        			+ "_number_format_short");
        	String longMessage = messages.getString("administration.invalid"
        			+ "_number_format_long");
        	FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
        			shortMessage, longMessage);
            throw new ConverterException(msg);
        }
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Duration duration) {
		double divResult = ((double) duration.getSeconds()) / (60 * 60);
        String resultValue = 
        	  String.valueOf(Math.toIntExact(Math.round(Math.ceil(divResult))));
		return resultValue;
	}

}
