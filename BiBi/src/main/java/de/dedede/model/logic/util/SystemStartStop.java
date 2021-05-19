package de.dedede.model.logic.util;

import jakarta.faces.event.AbortProcessingException;
import jakarta.faces.event.SystemEvent;
import jakarta.faces.event.SystemEventListener;

import java.util.EventListener;

public class SystemStartStop implements SystemEventListener {

	@Override
	public void processEvent(SystemEvent systemEvent) throws AbortProcessingException {

	}

	@Override
	public boolean isListenerForSource(Object o) {
		return false;
	}
}
