package gamePieces;

public class Card {
	private String id;
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
	private int height;

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
	}

	/**
	 * This is here temprarly
	 */
	public Card() {
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

	public int getHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}
	public void changeHeight(int heightChange) {
		this.height += heightChange;
	}

	
	/*
	 *************************************
	 * Thread no futher, there is nothing 
	 * here but the vilest of getters ahead.
	 ************************************
	 */

	public String getId() {
		return id;
	}

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
