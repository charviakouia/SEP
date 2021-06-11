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

@FacesConverter(value = "ListOfURLs")
public class ListOfURLsConverter implements Converter<List<URL>> {

    @Override
    public List<URL> getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        String[] urlStrings = s.trim().split("\\s*,\\s*");
        List<URL> result = new LinkedList<>();
        try {
            for (String urlString : urlStrings){
                result.add(new URL(urlString));
            }
            return result;
        } catch (MalformedURLException e) {
            throw new ConverterException("Couldn't convert a given url", e);
        }
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, List<URL> urls) {
        StringJoiner sj = new StringJoiner(", ");
        for (URL url : urls){ sj.add(url.toString()); }
        return sj.toString();
    }

}
