package database;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.stream.Stream;

public class CardChooser {

	private Iterator<String> it;
	private Stream<String> cardStream;

	public CardChooser( String filename ) throws IOException {
		String filepathString = 
			"decks/" +
			filename;

		Path filepath = Paths.get(filepathString);

		cardStream = Files.lines(filepath, StandardCharsets.UTF_8);
		// DO NOT HAVE LEADING WHITESPACE!
		cardStream = cardStream
			.filter( u -> u.charAt(0) != '#' ) // '#' for comment
			.sorted();                         // Alphabetical

		it = cardStream.iterator();


	}
	public String next() {
		if( it.hasNext() ) {
			return it.next();
		} else {
			cardStream.close();
			return "0";
		}
	}
	public boolean hasNext() {
		return it.hasNext();
	}
}
