package database;

import java.io.IOException;

/**
 * Allows for a String[] to behave like a bad iterrator
 * @param cardList the String[] that should behave a bit like an iterrator
 */
public class CardChooser {

	private String[] cardList;
	private int position;

	public CardChooser( String[] cardList ) throws IOException {
		this.cardList = cardList;
		position = 0;
	}
	public String next() {
		if( position < cardList.length ) {
			return cardList[position++];
		} else {
			return "0";
		}
	}
	public boolean hasNext() {
		return position < cardList.length;
	}
}
