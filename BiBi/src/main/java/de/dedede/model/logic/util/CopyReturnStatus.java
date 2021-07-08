package de.dedede.model.logic.util;

/**
 * Used to transmit copy-return process validation status information to the 
 * @see ReturnFormSignatureValidator in order to avoid using the same control 
 * flow via exceptions the validator-interface already uses.
 * 
 * @author Jonas Picker
 */
public enum CopyReturnStatus {
	
	SIGNATURE_NOT_FOUND, COPY_NOT_LENT, WRONG_USER, RETURN_DEADLINE_EXCEEDED
}
