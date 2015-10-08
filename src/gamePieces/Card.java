package gamePieces;

import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

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

	private double tablePosX;
	private double tablePosY;
	private double tablePosZ;
	private boolean isFaceUp;
	private double rotation;

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

		//===============================//
		//         JavaFX below          //
		//===============================//
		
		this.getStyleClass().add("card");
		this.setPrefSize(70, 100);
		Text testText = new Text(cardName);
		testText.setWrappingWidth(60);
		testText.setTranslateY(15);
		testText.setTranslateX(5);
		this.getChildren().add(testText);

		this.setOnMouseClicked(new MouseEventHandler());

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

	private class MouseEventHandler implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) {
			// A click changes between 'taped' & 'un-taped'
			if(rotation == 0) {
				setRotation(90d);
			} else {
				setRotation(0d);
			}
		}
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
	public void setTablePosX(double tablePosX) {
		this.tablePosX = tablePosX;
		this.setTranslateX(this.tablePosX);
	}


	public void modifyTablePosX(double changeX) {
		this.tablePosX += changeX;
		this.setTranslateX(this.tablePosX);
	}

	/**
	 * @param tablePosY the tablePosY to set
	 */
	public void setTablePosY(double tablePosY) {
		this.tablePosY = tablePosY;
		this.setTranslateY(this.tablePosY);
	}

	public void modifyTablePosY(double changeY) {
		this.tablePosY += changeY;
		this.setTranslateY(this.tablePosY);
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
		this.setRotate(this.rotation);
	}
	public void rotate(double rotation) {
		this.rotation += rotation;
		this.setRotate(this.rotation);
	}

	public double getTablePosZ() {
		return tablePosZ;
	}

	/**
	 * @param tablePosZ the tablePosZ to set
	 */
	public void setTablePosZ(double tablePosZ) {
		this.tablePosZ = tablePosZ;
	}
	public void changeTablePosZ(double tablePosZChange) {
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

	public double getTablePosX() {
		return tablePosX;
	}

	public double getTablePosY() {
		return tablePosY;
	}

}
