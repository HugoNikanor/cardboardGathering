package serverPackets;

import gamePieces.CardCollection;

public class CardBetweenCollectionsPacket extends NetworkPacket {
	private static final long serialVersionUID = 1L;

	private CardCollection.Collections oldCollection;
	private CardCollection.Collections newCollection;
	private long id;

	public CardBetweenCollectionsPacket( CardCollection.Collections oldCollection, CardCollection.Collections newCollection, long cardId ) {
		this.oldCollection = oldCollection;
		this.newCollection = newCollection;
		this.id = cardId;

		this.dataType = DataTypes.CARDCOLLECTIONCHANGE;
	}

	/**
	 * @return the oldCollection
	 */
	public CardCollection.Collections getOldCollection() {
		return oldCollection;
	}

	/**
	 * @return the newCollection
	 */
	public CardCollection.Collections getNewCollection() {
		return newCollection;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}
}
