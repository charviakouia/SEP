package de.dedede.model.logic.util;

import de.dedede.model.persistence.util.DataLayerInitializer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.event.AbortProcessingException;
import jakarta.faces.event.SystemEvent;
import jakarta.faces.event.SystemEventListener;

import java.util.EventListener;

/**
 *  Conducts and relays necessary actions before the system is shutdown or after it was started. Is registered in the faces-config.xml.
 */
@ApplicationScoped
public class SystemStartStop implements SystemEventListener {

	/**
	 * The data layer initializer
	 */
	private DataLayerInitializer initializer;

   /** {@inheritDoc}
   */
	@Override
	public void processEvent(SystemEvent systemEvent) throws AbortProcessingException {

	}

	/**
	 *{@inheritDoc}
	 *
	 */
	@Override
	public boolean isListenerForSource(Object o) {
		return false;
	}
}
