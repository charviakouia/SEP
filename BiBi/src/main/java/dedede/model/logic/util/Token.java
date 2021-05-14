package dedede.model.logic.util;

import java.time.LocalDateTime;

/**
 * A token used for email address verification and password resets.
 * Consists of a string of randomly generated characters and the point in
 * time when it was created since it has a limited span of life.
 *
 */
public class Token {

	private String content;
	
	private LocalDateTime creationTime;
}
