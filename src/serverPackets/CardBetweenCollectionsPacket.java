package serverPackets;

import gamePieces.Collections;

public class CardBetweenCollectionsPacket extends NetworkPacket {
	private static final long serialVersionUID = 1L;

	private Collections oldCollection;
	private Collections newCollection;
	private long id;

	public CardBetweenCollectionsPacket( Collections oldCollection, Collections newCollection, long cardId ) {
		this.oldCollection = oldCollection;
		this.newCollection = newCollection;
		this.id = cardId;

		this.dataType = DataTypes.CARDCOLLECTIONCHANGE;
	}

	/**
	 * @return the oldCollection
	 */
	public Collections getOldCollection() {
		return oldCollection;
	}

	/**
	 * @return the newCollection
	 */
	public Collections getNewCollection() {
		return newCollection;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}
}
