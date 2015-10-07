package gamePieces;

import javafx.scene.layout.Pane;

// TODO The Pane extension should be another type of pane
public class Card extends Pane {
	private String cardName;
	private String type;
	private String subtype;
	private String ability;
	private String flavour;

	private int power;
	private int toughness;
	private int loyalty;

	private int manaCostRed;
	private int manaCostBlue;
	private int manaCostWhite;
	private int manaCostBlack;
	private int manaCostGreen;
	private int manaCostBlank;
	private int convertedManaCost;

	private int tablePosX;
	private int tablePosY;
	private boolean isFaceUp;
	private double rotation;
	private int tablePosZ;

	public Card(
			String cardName,
			String type,
			String subtype,
			String ability,
			String flavour,
			int power,
			int toughness,
			int loyalty,
			int manaCostRed,
			int manaCostBlue,
			int manaCostWhite,
			int manaCostBlack,
			int manaCostGreen,
			int manaCostBlank
			) {
		//System.out.println("debug: start of Card");

		this.cardName  = cardName;
		this.type      = type;
		this.subtype   = subtype;
		this.ability   = ability;
		this.flavour   = flavour;
		this.power     = power;
		this.toughness = toughness;
		this.loyalty   = loyalty;

		this.manaCostRed   = manaCostRed;
        this.manaCostBlue  = manaCostBlue;
        this.manaCostWhite = manaCostWhite;
        this.manaCostBlack = manaCostBlack;
        this.manaCostGreen = manaCostGreen;
		this.manaCostBlank = manaCostBlank;
		calcConvMana();

		//System.out.println("debug: end of Card");
	}

	/**
	 * Adds up all the mana costs
	 * Automacicly asigns the output to 'convertedManaCost'
	 */
	private void calcConvMana() {
		convertedManaCost = 
			manaCostRed   + 
			manaCostBlue  + 
			manaCostGreen + 
			manaCostWhite + 
			manaCostBlack + 
			manaCostBlank;
	}

	/**
	 *************************************
	 * Functions for manipulating the physical 
	 * represontation of the card
	 ************************************
	 */

	/**
	 * @param tablePosX the tablePosX to set
	 */
	public void setTablePosX(int tablePosX) {
		this.tablePosX = tablePosX;
	}


	public void modifyTablePosX(int changeX) {
		this.tablePosX += changeX;
	}

	/**
	 * @param tablePosY the tablePosY to set
	 */
	public void setTablePosY(int tablePosY) {
		this.tablePosY = tablePosY;
	}

	public void modifyTablePosY(int changeY) {
		this.tablePosY += changeY;
	}

	public boolean isFaceUp() {
		return isFaceUp;
	}

	/**
	 * @param isFaceUp the isFaceUp to set
	 */
	public void setFaceUp(boolean isFaceUp) {
		this.isFaceUp = isFaceUp;
	}
	public void flip() {
		if(isFaceUp) {
			isFaceUp = false;
		} else {
			isFaceUp = true;
		}

	}

	public double getRotation() {
		return rotation;
	}

	/**
	 * @param rotation the rotation to set
	 */
	public void setRotation(double rotation) {
		this.rotation = rotation;
	}
	public void rotate(double rotation) {
		this.rotation += rotation;
	}

	public int gettablePosZ() {
		return tablePosZ;
	}

	/**
	 * @param tablePosZ the tablePosZ to set
	 */
	public void settablePosZ(int tablePosZ) {
		this.tablePosZ = tablePosZ;
	}
	public void changetablePosZ(int tablePosZChange) {
		this.tablePosZ += tablePosZChange;
	}

	@Override
	public String toString() {
		String returnString = 
			"cardName     : " + cardName      + "\n" +
			"type         : " + type          + "\n" +
			"subtype      : " + subtype       + "\n" +
			"ability      : " + ability       + "\n" +
			"flavour      : " + flavour       + "\n" +
			"power        : " + power         + "\n" +
			"toughness    : " + toughness     + "\n" +
			"loyalty      : " + loyalty       + "\n" +
			"manaCostRed  : " + manaCostRed   + "\n" +
			"manaCostBlue : " + manaCostBlue  + "\n" +
			"manaCostWhite: " + manaCostWhite + "\n" +
			"manaCostBlack: " + manaCostBlack + "\n" +
			"manaCostGreen: " + manaCostGreen + "\n" +
			"manaCostBlank: " + manaCostBlank + "\n";
		return returnString;
	}
	
	/*
	 *************************************
	 * Thread no futher, there is nothing 
	 * here but the vilest of getters ahead.
	 ************************************
	 */

	public String getCardName() {
		return cardName;
	}

	public String getType() {
		return type;
	}

	public String getSubtype() {
		return subtype;
	}

	public String getAbility() {
		return ability;
	}

	public String getFlavour() {
		return flavour;
	}

	public int getPower() {
		return power;
	}

	public int getToughness() {
		return toughness;
	}

	public int getLoyalty() {
		return loyalty;
	}

	public int getManaCostRed() {
		return manaCostRed;
	}

	public int getManaCostBlue() {
		return manaCostBlue;
	}

	public int getManaCostGreen() {
		return manaCostGreen;
	}

	public int getManaCostWhite() {
		return manaCostWhite;
	}

	public int getManaCostBlack() {
		return manaCostBlack;
	}

	public int getManaCostBlank() {
		return manaCostBlank;
	}

	public int getConvertedManaCost() {
		return convertedManaCost;
	}

	public int getTablePosX() {
		return tablePosX;
	}

	public int getTablePosY() {
		return tablePosY;
	}

}
