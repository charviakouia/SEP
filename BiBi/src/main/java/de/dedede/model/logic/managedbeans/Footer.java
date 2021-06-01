package de.dedede.model.logic.managedbeans;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * The footer has a text displayed for the user to see his login status with his
 * name.
 */

@Named
@Dependent
public class Footer {

    @Inject
    private UserSession currentUser;




}