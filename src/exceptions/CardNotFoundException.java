package exceptions;

/**
 * Error thrown when a card is not found
 * Used to centralize a bunch of other exceptions
 */
public class CardNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;

	public CardNotFoundException(String message) {
		super(message);
	}
}
