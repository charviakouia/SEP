package de.dedede.model.logic.converters;

import java.time.Duration;
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
@FacesConverter("decimalToDurationConverter")
public class DecimalToDurationConverter implements Converter<Duration> {
	
	/**
	 * Transforms a decimal number of days into a Duration object. 
	 * Throws an error message if the number was of invalid format or negative.
	 * 
	 * @param context the FacesContext.
	 * @param component the UI component this converter is registered on.
	 * @param input the number to be converted as a String.
	 * @return a Duration object representing the number of days in the String.
	 */
	@Override
	public Duration getAsObject(FacesContext context, UIComponent component, 
			String input) {
		Application application = context.getApplication();
		ResourceBundle messages = application.evaluateExpressionGet(context, 
				"#{msg}", ResourceBundle.class);
		try {
            double numberOfDays = Double.valueOf(input);
            if (numberOfDays < 0) {
            	String shortMessage = messages.getString("administration.no"
            			+ "_negatives_short");
            	String longMessage = messages.getString("administration.no"
            			+ "_negatives_long");
            	FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
            			shortMessage, longMessage);
            	throw new ConverterException(msg);
            } else {
            	double numberOfNanos = 
            			numberOfDays * Duration.ofDays(1).toNanos();
            	
            	return Duration.ofNanos(Math.round(numberOfNanos));
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
	
	/**
	 * Converts the time interval held in the Duration object into a decimal 
	 * number representing days.
	 * 
	 * @param context the FacesContext.
	 * @param component the UI component this converter is registered on.
	 * @param duration the Duration object to be converted into a decimal.
	 */
	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Duration duration) {
		double divResult = ((double) duration.getSeconds()) / (60 * 60 * 24);
        String resultValue = String.valueOf(divResult);
        
		return resultValue;
	}

}
