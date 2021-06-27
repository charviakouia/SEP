package de.dedede.model.logic.util;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

import java.util.ResourceBundle;

public class MessagingUtility {

    public static void writePositiveMessage(FacesContext context, String message){
        context.addMessage("messageForm:positive", new FacesMessage(message));
    }

    public static void writePositiveMessageWithKey(FacesContext context, String key, Object... params){
        writePositiveMessage(context, getMessage(context, key, params));
    }

    public static void writeNegativeMessage(FacesContext context, String message){
        context.addMessage("messageForm:negative", new FacesMessage(message));
    }

    public static void writeNegativeMessageWithKey(FacesContext context, String key, Object... params){
        writeNegativeMessage(context, getMessage(context, key, params));
    }

    private static String getMessage(FacesContext context, String key, Object... params){
        ResourceBundle bundle;
        bundle = context.getApplication().evaluateExpressionGet(context, "#{msg}", ResourceBundle.class);
        String message = String.format(bundle.getString(key), params);
        return message;
    }

}
