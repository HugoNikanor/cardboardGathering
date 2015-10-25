package database;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.stream.Stream;

public class DeckCreator {

	Iterator<String> it;
	Stream<String> cardStream;

	public DeckCreator( String filename ) throws IOException {
		String filepathString = 
			"/home/hugo/code/java/cardboardGathering/database/" +
			filename;

		Path filepath = Paths.get(filepathString);

		cardStream = Files.lines(filepath, StandardCharsets.UTF_8);
		it = cardStream.iterator();

		//cardStream.forEach(System.out::println);

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
	public long getCount() {
		return 1;
	}
}
