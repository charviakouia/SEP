package de.dedede.model.logic.converters;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.convert.FacesConverter;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@FacesConverter(value = "DurationConverter")
public class DurationConverter implements Converter<Duration> {

    @Override
    public Duration getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        try {
            int numDays = Integer.parseInt(s);
            if (numDays < 0) {
            	throw new ConverterException("Number of days cannot be negative");
            } else {
            	return Duration.of(Integer.toUnsignedLong(numDays), ChronoUnit.DAYS);
            }
        } catch (NumberFormatException e){
            throw new ConverterException("Couldn't read the duration", e);
        }
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Duration duration) {
        double divResult = ((double) duration.getSeconds()) / (60 * 60 * 24);
        return String.valueOf(Math.toIntExact(Math.round(Math.ceil(divResult))));
    }
}
