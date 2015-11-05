package inputObjects;

import java.util.stream.Stream;

public class CardListObject extends NetworkPacket {
	private static final long serialVersionUID = 1L;

	Stream<String> stream;

	public CardListObject( Stream<String> stream ) {
		this.stream = stream;

		this.dataType = DataTypes.CARDLIST;
		this.data = this;
	}

	/**
	 * @return the stream
	 */
	public Stream<String> getStream() {
		return stream;
	}

}
