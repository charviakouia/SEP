package de.dedede.model.logic.util;

import jakarta.faces.application.Application;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ValueChangeEvent;
import jakarta.inject.Inject;

import java.util.ResourceBundle;

public enum MediumType {

    BOOK("mediumType.book", "Book"),
    ARTICLE("mediumType.article", "Article"),
    CD("mediumType.cd", "CD");

    private String key;
    private String canonicalName;

    MediumType(String key, String canonicalName){
        this.key = key;
        this.canonicalName = canonicalName;
    }

    public String getCanonicalName(){
        return canonicalName;
    }

    public String getInternationalName(FacesContext context){
        Application app = context.getApplication();
        ResourceBundle messages = app.evaluateExpressionGet(context, "#{msg}", ResourceBundle.class);
        return messages.getString(key);
    }

}
