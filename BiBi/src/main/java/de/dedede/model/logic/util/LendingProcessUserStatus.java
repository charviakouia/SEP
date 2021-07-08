package de.dedede.model.logic.util;

/**
 * Used to transmit the lending process validation status of the user to the 
 * @see UserExistsAndCanLendValidator in order to avoid using the same 
 * control flow via exceptions the validator interface already uses.
 * 
 * @author Jonas Picker
 */
public enum LendingProcessUserStatus {

	EMAIL_NOT_FOUND, USER_CANNOT_LEND
}
