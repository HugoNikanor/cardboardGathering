package serverPackets;

import gamePieces.CardCollection;

public class CardBetweenCollectionsPacket extends NetworkPacket {
	private static final long serialVersionUID = 1L;

	private CardCollection.CollectionTypes oldCollection;
	private CardCollection.CollectionTypes newCollection;
	private long id;

	public CardBetweenCollectionsPacket( CardCollection.CollectionTypes oldCollection, CardCollection.CollectionTypes newCollection, long cardId ) {
		this.oldCollection = oldCollection;
		this.newCollection = newCollection;
		this.id = cardId;

		this.dataType = DataTypes.CARDCOLLECTIONCHANGE;
	}

	/**
	 * @return the oldCollection
	 */
	public CardCollection.CollectionTypes getOldCollection() {
		return oldCollection;
	}

	/**
	 * @return the newCollection
	 */
	public CardCollection.CollectionTypes getNewCollection() {
		return newCollection;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}
}
