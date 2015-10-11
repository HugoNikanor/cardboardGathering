package gamePieces;

import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import javafx.animation.RotateTransition;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Duration;

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

	enum Field {
		manaCostBlank,
		manaCostBlack,
		manaCostBlue,
		manaCostGreen,
		manaCostRed,
		manaCostWhite,
	}
	private int manaCostBlank;

	private int manaCostBlack;
	private int manaCostBlue;
	private int manaCostGreen;
	private int manaCostRed;
	private int manaCostWhite;

	private int convertedManaCost;

	private boolean isFaceUp;

	private double containerSizeX;
	private double containerSizeY;

	// Clean up X & Y
	private double scaleFactorX;
	private double scaleFactorY;

	private static Card currentCard;

	//public Card() {}

	public Card(
		String cardName,
		String type,
		String subtype,
		String ability,
		String flavour,
		int power,
		int toughness,
		int loyalty,
		int manaCostBlack,
		int manaCostBlue,
		int manaCostGreen,
		int manaCostRed,
		int manaCostWhite,
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

        this.manaCostBlack = manaCostBlack;
        this.manaCostBlue  = manaCostBlue;
        this.manaCostGreen = manaCostGreen;
		this.manaCostRed   = manaCostRed;
        this.manaCostWhite = manaCostWhite;
		this.manaCostBlank = manaCostBlank;

		this.calcConvMana();

		//===============================//
		//         JavaFX below          //
		//===============================//
		
		this.getStyleClass().add("card");

		//width = 70;
		//height = 100;
		this.setHeight(150);
		this.setWidth(0.7 * 150);
		this.setPrefSize(this.getWidth(), this.getHeight());

		Text cardNameText = new Text(cardName);
		cardNameText.setWrappingWidth(60);
		cardNameText.setTranslateY(15);
		cardNameText.setTranslateX(5);

		//Label nameLabel = new Label(cardName);
		this.getChildren().add(cardNameText);

		this.setCursor(Cursor.HAND);

		currentCard = this;

		MouseEventHandler mouseEventHandler = new MouseEventHandler();

		//this.setOnMouseClicked ( mouseEventHandler );
		this.setOnMouseDragged ( mouseEventHandler );
		this.setOnMousePressed ( mouseEventHandler );
		this.setOnMouseReleased( mouseEventHandler );

		this.setOnScroll( new ScrollEventHandler() );

		scaleFactorX = 1;
		scaleFactorY = 1;

		containerSizeX = 1600 - this.getWidth();
		containerSizeY = 395 - this.getHeight();

		//System.out.println("debug: end of Card");
	}


	/**
	 * Adds up all the mana costs
	 * Automacicly asigns the output to 'convertedManaCost'
	 */
	private void calcConvMana() {
		convertedManaCost = 
			manaCostBlack + 
			manaCostBlue  + 
			manaCostGreen + 
			manaCostRed   + 
			manaCostWhite + 
			manaCostBlank;

		Field largestField = Field.manaCostBlank;
		int largestValue = 0;
		if( manaCostBlack > largestValue ) {
			largestField = Field.manaCostBlack;
			largestValue = manaCostBlack;
		}
		if( manaCostBlue > largestValue ) {
			largestField = Field.manaCostBlue;
			largestValue = manaCostBlue;
		}
		if( manaCostGreen > largestValue ) {
			largestField = Field.manaCostGreen;
			largestValue = manaCostGreen;
		}
		if( manaCostRed > largestValue ) {
			largestField = Field.manaCostRed;
			largestValue = manaCostRed;
		}
		if( manaCostWhite > largestValue ) {
			largestField = Field.manaCostWhite;
			largestValue = manaCostWhite;
		}
		switch( largestField ) {
			case manaCostBlank:
				System.out.println("blank");
				this.getStyleClass().add("color-less");
			break;
			case manaCostBlack:
				System.out.println("black");
				this.getStyleClass().add("black");
			break;
			case manaCostBlue:
				System.out.println("blue");
				this.getStyleClass().add("blue");
			break;
			case manaCostGreen:
				System.out.println("green");
				this.getStyleClass().add("green");
			break;
			case manaCostRed:
				System.out.println("red");
				this.getStyleClass().add("red");
			break;
			case manaCostWhite:
				System.out.println("white");
				this.getStyleClass().add("white");
			break;

		}
	}

	private class MouseEventHandler implements EventHandler<MouseEvent> {
		private double mouseInSceneX;
		private double mouseInSceneY;
		private EventType<? extends MouseEvent> lastEvent;

		@Override
		public void handle(MouseEvent event) {
			if( event.getEventType() == MouseEvent.MOUSE_PRESSED ) {
				Card.this.giveThisFocus();

				this.mouseInSceneX = event.getSceneX();
				this.mouseInSceneY = event.getSceneY();

				Card.this.setCursor(Cursor.MOVE);
			}

			/*
			 * Rotates the card if it's clicked and not draged
			 */
			if( event.getEventType() == MouseEvent.MOUSE_RELEASED &&
				this.lastEvent == MouseEvent.MOUSE_PRESSED ) {
				Card.this.smoothRotate(90d);
			}

			if( event.getEventType() == MouseEvent.MOUSE_PRESSED ) {
				Card.this.setCursor(Cursor.HAND);
			}

			if( event.getEventType() == MouseEvent.MOUSE_DRAGGED ) {
				double xChange = event.getSceneX() - this.mouseInSceneX;
				double yChange = event.getSceneY() - this.mouseInSceneY;

				//System.out.println(scaleFactorX);
				//System.out.print( "sfY C: " + scaleFactorY);
				//System.out.println( "sfX C: " + scaleFactorX);

				Card.this.setTranslateX(getTranslateX() + xChange * ( 1/scaleFactorX ));
				Card.this.setTranslateY(getTranslateY() + yChange * ( 1/scaleFactorX ));

				if( getTranslateX() < 0 ) {
					setTranslateX(0);
				}
				if( getTranslateY() < 0 ) {
					setTranslateY(0);
				}
				if( getTranslateX() > containerSizeX ) {
					setTranslateX(containerSizeX);
				}
				if( getTranslateY() > containerSizeY ) {
					setTranslateY(containerSizeY);
				}

				this.mouseInSceneX = event.getSceneX();
				this.mouseInSceneY = event.getSceneY();

				/*
				System.out.print(" trX: " + Card.this.getTranslateX());
				System.out.print(" trY: " + Card.this.getTranslateY());
				System.out.print(" cgpX: " + mouseInSceneX);
				System.out.print(" cgpY: " + mouseInSceneY);
				System.out.println();
				*/
			}

			this.lastEvent = event.getEventType();
		}
	}

	private class ScrollEventHandler implements EventHandler<ScrollEvent> {
		@Override
		public void handle(ScrollEvent event) {
			Card.this.giveThisFocus();
			/**
			 * Scales card by a factor 2 when scrolling over it,
			 * please be avare that if you make the card to small then it 
			 * dissapears.
			 */
			if( event.getDeltaY() > 0 ) {
				setScaleX(getScaleX() * (event.getDeltaY() / 20));
				setScaleY(getScaleY() * (event.getDeltaY() / 20));
			} else {
				setScaleX(getScaleX() * (1 / (-1 * event.getDeltaY() / 20)));
				setScaleY(getScaleY() * (1 / (-1 * event.getDeltaY() / 20)));
			}
		}
	}

	/*************************************
	 * Functions for manipulating the physical 
	 * represontation of the card
	 ************************************/

	/**
	 * Rotates the card to 'rotation' if the rotation is 0
	 * if the rotation isn't 0 the change it to zero
	 */
	public void smoothRotate( double rotation ) {
		RotateTransition rt;
		if( Card.this.getRotate() == 0 ) {
			// Rotates the card by 90 degrees
			// Always rotates clockwise
			rt = new RotateTransition(Duration.millis(300), Card.this);
			rt.setByAngle(rotation);
			rt.play();
		} else {
			// if the rotation isn't 90 degrees then return to 0 degrees
			// always rotates counter clockwise
			rt = new RotateTransition(Duration.millis(300), Card.this);
			rt.setByAngle( -1 * Card.this.getRotate() );
			rt.play();
		}
	}

	public void giveThisFocus() {
		currentCard.setId(null);
		currentCard = this;
		currentCard.setId("has-focus");
	}

	public void modifyTranslateX(double change) {
		this.setTranslateX(this.getTranslateX() + change);
	}
	public void modifyTranslateY(double change) {
		this.setTranslateY(this.getTranslateY() + change);
	}

	public void modifyRotate(double change) {
		this.setRotate(this.getRotate() + change);
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

	/**
	 * @param containerSizeX the containerSizeX to set
	 */
	public void setContainerSizeX(double containerSizeX) {
		this.containerSizeX = containerSizeX;
	}

	/**
	 * @param containerSizeY the containerSizeY to set
	 */
	public void setContainerSizeY(double containerSizeY) {
		this.containerSizeY = containerSizeY;
	}

	/**
	 * @param scaleFactorX the scaleFactorX to set
	 */
	public void setScaleFactorX(double scaleFactorX) {
		this.scaleFactorX = scaleFactorX;
	}

	/**
	 * @param scaleFactorY the scaleFactorY to set
	 */
	public void setScaleFactorY(double scaleFactorY) {
		this.scaleFactorY = scaleFactorY;
	}

	/**
	 * @return the currentCard
	 */
	public static Card getCurrentCard() {
		return currentCard;
	}

	public void flip() {
		if(isFaceUp) {
			isFaceUp = false;
		} else {
			isFaceUp = true;
		}
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

}
