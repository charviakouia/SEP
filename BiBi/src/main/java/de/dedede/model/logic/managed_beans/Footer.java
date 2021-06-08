package de.dedede.model.logic.managed_beans;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

/**
 * The footer has a text displayed for the user to see his login status with his
 * name.
 */

@Named
@RequestScoped
public class Footer {

    @PostConstruct
	public void init() {
    }
}