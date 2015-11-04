package inputObjects;

public class CardMoveObject {

	private long id;
	private double changeX;
	private double changeY;

	public CardMoveObject( long cardId, double changeX, double changeY ) {
		this.id = cardId;
		this.changeX = changeX;
		this.changeY = changeY;
	}


	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @return the changeX
	 */
	public double getChangeX() {
		return changeX;
	}

	/**
	 * @return the changeY
	 */
	public double getChangeY() {
		return changeY;
	}
}
