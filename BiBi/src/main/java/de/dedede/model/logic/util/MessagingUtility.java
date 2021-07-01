package de.dedede.model.logic.util;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class MessagingUtility {

    public static void writePositiveMessage(FacesContext context, boolean fScope, String message){
        context.addMessage("messageForm:positive", new FacesMessage(message));
        if (fScope){
            context.getExternalContext().getFlash().setKeepMessages(true);
        }
    }

    public static void writePositiveMessageWithKey(FacesContext context, boolean fScope, String key, Object... params){
        writePositiveMessage(context, fScope, getMessage(context, key, params));
    }

    public static void writeNegativeMessage(FacesContext context, boolean fScope, String message){
        context.addMessage("messageForm:negative", new FacesMessage(message));
        if (fScope){
            context.getExternalContext().getFlash().setKeepMessages(true);
        }
    }

    public static void writeNegativeMessageWithKey(FacesContext context, boolean fScope, String key, Object... params){
        writeNegativeMessage(context, fScope, getMessage(context, key, params));
    }

    public static void writeNeutralMessage(FacesContext context, boolean fScope, String message){
        context.addMessage("messageForm:neutral", new FacesMessage(message));
        if (fScope){
            context.getExternalContext().getFlash().setKeepMessages(true);
        }
    }

    public static void writeNeutralMessageWithKey(FacesContext context, boolean fScope, String key, Object... params){
        writeNeutralMessage(context, fScope, getMessage(context, key, params));
    }

    public static String getMessage(FacesContext context, String key, Object... params){
        ResourceBundle bundle;
        bundle = context.getApplication().evaluateExpressionGet(context, "#{msg}", ResourceBundle.class);
        MessageFormat mf = new MessageFormat(bundle.getString(key));
        return mf.format(params, new StringBuffer(), null).toString();
    }

}
