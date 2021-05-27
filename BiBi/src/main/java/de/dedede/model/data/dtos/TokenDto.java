package de.dedede.model.data.dtos;

import java.time.LocalDateTime;

/**
 * A token used for email address verification and password resets. Consists of
 * a string of randomly generated characters and the point in time when it was
 * created since it has a limited span of life.
 *
 */
public class TokenDto {

	private String content;

	private LocalDateTime creationTime;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public LocalDateTime getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(LocalDateTime creationTime) {
		this.creationTime = creationTime;
	}
	
	
}
