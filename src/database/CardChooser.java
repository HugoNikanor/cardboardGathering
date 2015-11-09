package database;

/**
 * Allows for a String[] to behave like a bad iterrator
 */
public class CardChooser {

	/**
	 * All the card names
	 */
	private String[] cardList;

	/**
	 * Which card should be returned next
	 */
	private int position;

	/**
	 * @param cardList the String[] that should behave a bit like an iterrator
	 */
	public CardChooser( String[] cardList ) {
		this.cardList = cardList;
		position = 0;
	}

	/**
	 * @return the next card in the list
	 */
	public String next() {
		if( position < cardList.length ) {
			return cardList[position++];
		} else {
			return "0";
		}
	}

	/**
	 * @return if there are any more cards in the list
	 */
	public boolean hasNext() {
		return position < cardList.length;
	}
}
