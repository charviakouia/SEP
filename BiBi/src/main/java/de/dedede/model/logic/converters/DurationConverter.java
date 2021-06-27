package de.dedede.model.logic.converters;

import de.dedede.model.logic.util.MessagingUtility;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.convert.FacesConverter;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * Converts durations, as represented by the Duration class {@link Duration}, into a textual representation and
 * vice-versa. In particular, the textual representation displays the number of days.
 *
 * @author Ivan Charviakou
 */
@FacesConverter(value = "DurationConverter")
public class DurationConverter implements Converter<Duration> {

	private static final String NO_NEG_S = "administration.no_negatives_short";
	private static final String NO_NEG_L = "administration.no_negatives_long";

	private static final String INV_NUM_S = "administration.invalid_number_format_short";
	private static final String INV_NUM_L = "administration.invalid_number_format_long";

    @Override
    public Duration getAsObject(FacesContext context, UIComponent uiComponent, String s) {
    	if (s == null || s.isBlank()){
    		return null;
		}
        try {
            int numDays = Integer.parseInt(s);
            if (numDays < 0) {
				String shortMessage = MessagingUtility.getMessage(context, NO_NEG_S);
				String longMessage = MessagingUtility.getMessage(context, NO_NEG_L);
            	FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, shortMessage, longMessage);
            	throw new ConverterException(msg);
            } else {
            	return Duration.of(Integer.toUnsignedLong(numDays), ChronoUnit.DAYS);
            }
        } catch (NumberFormatException e){
        	String shortMessage = MessagingUtility.getMessage(context, INV_NUM_S);
        	String longMessage = MessagingUtility.getMessage(context, INV_NUM_L);
        	FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, shortMessage, longMessage);
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
