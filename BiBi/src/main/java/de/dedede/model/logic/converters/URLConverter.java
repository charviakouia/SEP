package de.dedede.model.logic.converters;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.convert.FacesConverter;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

@FacesConverter(value = "URLs")
public class URLConverter implements Converter<URL> {

    @Override
    public URL getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        try {
            String urlString = s.trim();
            return new URL(urlString);
        } catch (MalformedURLException e) {
            throw new ConverterException("Couldn't convert a given url", e);
        }
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, URL url) {
        return url.toString();
    }

}
