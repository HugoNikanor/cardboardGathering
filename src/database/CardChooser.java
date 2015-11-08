package database;

import java.io.IOException;
import java.util.Iterator;
import java.util.stream.Stream;

/**
 * class that converts a Stream<String> into an Iterator<String>
 *
 * This class should maybe be removed
 */
public class CardChooser {

	private Iterator<String> it;
	private Stream<String> cardListStream;

	public CardChooser( Stream<String> cardListStream ) throws IOException {
		this.cardListStream = cardListStream;
		it = cardListStream.iterator();
	}
	public String next() {
		if( it.hasNext() ) {
			return it.next();
		} else {
			cardListStream.close();
			return "0";
		}
	}
	public boolean hasNext() {
		return it.hasNext();
	}
}
