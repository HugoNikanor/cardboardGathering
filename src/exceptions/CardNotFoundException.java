package exceptions;

public class CardNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;

	public CardNotFoundException(String message) {
		super(message);
	}
}
