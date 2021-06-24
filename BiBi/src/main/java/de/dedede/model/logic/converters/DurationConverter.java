package de.dedede.model.logic.converters;

import jakarta.faces.application.Application;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.convert.FacesConverter;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

@FacesConverter(value = "DurationConverter")
public class DurationConverter implements Converter<Duration> {

    @Override
    public Duration getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
    	if (s == null || s.isBlank()){
    		return null;
		}
    	Application application = facesContext.getApplication();
		ResourceBundle messages = application.evaluateExpressionGet(facesContext, 
				"#{msg}", ResourceBundle.class);
        try {
            int numDays = Integer.parseInt(s);
            if (numDays < 0) {
            	String shortMessage = messages.getString("administration.no"
            			+ "_negatives_short");
            	String longMessage = messages.getString("administration.no"
            			+ "_negatives_long");
            	FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
            			shortMessage, longMessage);
            	throw new ConverterException(msg);
            } else {
            	return Duration.of(Integer.toUnsignedLong(numDays), ChronoUnit.DAYS);
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
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Duration duration) {
    	if (duration == null){
    		return null;
		}
        double divResult = ((double) duration.getSeconds()) / (60 * 60 * 24);
        return String.valueOf(Math.toIntExact(Math.round(Math.ceil(divResult))));
    }
}
