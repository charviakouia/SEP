package de.dedede.model.data.dtos;

/**
 * This DTO (data transfer object) is responsible for aggregating and
 * encapsulating data about a error for transfer.
 * <p>
 * See the {@link de.dedede.model.logic.managedbeans.Error} class to which this DTO is passed.
 *
 * @author Sergei Pravdin
 */
public class ErrorDto {

    private String message;

    /**
     * Fetches an error message to the user explaining what went wrong.
     *
     * @return A error message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets an error message to the user explaining what went wrong.
     *
     * @param message A error message.
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
