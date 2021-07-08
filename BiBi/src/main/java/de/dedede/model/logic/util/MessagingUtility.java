package de.dedede.model.logic.util;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Utility class for getting messages and/or writing them to the page header with a certain semantic meaning.
 * When the messages aren't supplied in plaintext, they are queried from the application's main resource bundle
 * using a supplied key.
 *
 * @author Ivan Charviakou
 */
public class MessagingUtility {

    /**
     * Fetches and writes a plaintext message. The message is displayed as positive.
     *
     * @param context JSF's faces-context
     * @param fScope Whether or not the message should be stored in the flash-scope, necessary for a change in view
     * @param message The message to be written.
     */
    public static void writePositiveMessage(FacesContext context, boolean fScope, String message){
        context.addMessage("messageForm:positive", new FacesMessage(message));
        if (fScope){
            context.getExternalContext().getFlash().setKeepMessages(true);
        }
    }

    /**
     * Fetches and writes a message from the app's main resource bundle, identified by the given key.
     * The message is displayed as positive.
     *
     * @param context JSF's faces-context
     * @param fScope Whether or not the message should be stored in the flash-scope, necessary for a change in view
     * @param key The key under which the resource bundle message is registered.
     * @param params The values with which parameters in the given message are substituted
     */
    public static void writePositiveMessageWithKey(FacesContext context, boolean fScope, String key, Object... params){
        writePositiveMessage(context, fScope, getMessage(context, key, params));
    }

    /**
     * Fetches and writes a plaintext message. The message is displayed as negative.
     *
     * @param context JSF's faces-context
     * @param fScope Whether or not the message should be stored in the flash-scope, necessary for a change in view
     * @param message The message to be written.
     */
    public static void writeNegativeMessage(FacesContext context, boolean fScope, String message){
        context.addMessage("messageForm:negative", new FacesMessage(message));
        if (fScope){
            context.getExternalContext().getFlash().setKeepMessages(true);
        }
    }

    /**
     * Fetches and writes a message from the app's main resource bundle, identified by the given key.
     * The message is displayed as negative.
     *
     * @param context JSF's faces-context
     * @param fScope Whether or not the message should be stored in the flash-scope, necessary for a change in view
     * @param key The key under which the resource bundle message is registered.
     * @param params The values with which parameters in the given message are substituted
     */
    public static void writeNegativeMessageWithKey(FacesContext context, boolean fScope, String key, Object... params){
        writeNegativeMessage(context, fScope, getMessage(context, key, params));
    }

    /**
     * Fetches and writes a plaintext message. The message is displayed as neutral.
     *
     * @param context JSF's faces-context
     * @param fScope Whether or not the message should be stored in the flash-scope, necessary for a change in view
     * @param message The message to be written.
     */
    public static void writeNeutralMessage(FacesContext context, boolean fScope, String message){
        context.addMessage("messageForm:neutral", new FacesMessage(message));
        if (fScope){
            context.getExternalContext().getFlash().setKeepMessages(true);
        }
    }

    /**
     * Fetches and writes a message from the app's main resource bundle, identified by the given key.
     * The message is displayed as neutral.
     *
     * @param context JSF's faces-context
     * @param fScope Whether or not the message should be stored in the flash-scope, necessary for a change in view
     * @param key The key under which the resource bundle message is registered.
     * @param params The values with which parameters in the given message are substituted
     */
    public static void writeNeutralMessageWithKey(FacesContext context, boolean fScope, String key, Object... params){
        writeNeutralMessage(context, fScope, getMessage(context, key, params));
    }

    /**
     * Fetches a message-string from the application's main resource bundle using a given key.
     *
     * @param context JSF's faces-context
     * @param key The key under which the resource bundle message is registered.
     * @param params The values with which parameters in the given message are substituted
     * @return A formatted message-string from the main resource bundle with the given parameters
     */
    public static String getMessage(FacesContext context, String key, Object... params){
        ResourceBundle bundle;
        bundle = context.getApplication().evaluateExpressionGet(context, "#{msg}", ResourceBundle.class);
        MessageFormat mf = new MessageFormat(bundle.getString(key));
        return mf.format(params, new StringBuffer(), null).toString();
    }

}
