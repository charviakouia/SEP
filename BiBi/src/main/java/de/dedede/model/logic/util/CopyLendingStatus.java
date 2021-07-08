package de.dedede.model.logic.util;

/**
 * Used to transmit copy-lending process validation status information to the 
 * @see LendingProcessSignatureValidator in order to avoid using the same 
 * control flow via exceptions the validator interface already uses.
 * 
 * @author Jonas Picker
 */
public enum CopyLendingStatus {
	
	SIGNATURE_NOT_FOUND, COPY_ALREADY_LENT, WRONG_USER
}
