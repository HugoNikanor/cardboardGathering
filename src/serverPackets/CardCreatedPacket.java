package serverPackets;

import gamePieces.Card;

public class CardCreatedPacket extends NetworkPacket {
	private static final long serialVersionUID = 1L;

	private String cardName;
	private String type;
	private String subtype;
	private String ability;
	private String flavour;
	private int power;
	private int toughness;
	private int loyalty;
	private int manaCostBlack;
	private int manaCostBlue;
	private int manaCostGreen;
	private int manaCostRed;
	private int manaCostWhite;
	private int manaCostBlank;
	private int manaCostX;

	public CardCreatedPacket( Card card ) {
		cardName      = card.getCardName();
		type          = card.getType();
		subtype       = card.getType();
		ability       = card.getAbility();
		flavour       = card.getFlavour();
		power         = card.getPower();
		toughness     = card.getToughness();
		loyalty       = card.getLoyalty();
		manaCostBlack = card.getManaCostBlack();
		manaCostBlue  = card.getManaCostBlue();
		manaCostGreen = card.getManaCostGreen();
		manaCostRed   = card.getManaCostRed();
		manaCostWhite = card.getManaCostWhite();
		manaCostBlank = card.getManaCostBlank();
		manaCostX     = card.getManaCostX();

		this.dataType = DataTypes.CARDCREATE;
	}

	/**
	 * @return the cardName
	 */
	public String getCardName() {
		return cardName;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the subtype
	 */
	public String getSubtype() {
		return subtype;
	}

	/**
	 * @return the ability
	 */
	public String getAbility() {
		return ability;
	}

	/**
	 * @return the flavour
	 */
	public String getFlavour() {
		return flavour;
	}

	/**
	 * @return the power
	 */
	public int getPower() {
		return power;
	}

	/**
	 * @return the toughness
	 */
	public int getToughness() {
		return toughness;
	}

	/**
	 * @return the loyalty
	 */
	public int getLoyalty() {
		return loyalty;
	}

	/**
	 * @return the manaCostBlack
	 */
	public int getManaCostBlack() {
		return manaCostBlack;
	}

	/**
	 * @return the manaCostBlue
	 */
	public int getManaCostBlue() {
		return manaCostBlue;
	}

	/**
	 * @return the manaCostGreen
	 */
	public int getManaCostGreen() {
		return manaCostGreen;
	}

	/**
	 * @return the manaCostRed
	 */
	public int getManaCostRed() {
		return manaCostRed;
	}

	/**
	 * @return the manaCostWhite
	 */
	public int getManaCostWhite() {
		return manaCostWhite;
	}

	/**
	 * @return the manaCostBlank
	 */
	public int getManaCostBlank() {
		return manaCostBlank;
	}

	/**
	 * @return the manaCostX
	 */
	public int getManaCostX() {
		return manaCostX;
	}
}
