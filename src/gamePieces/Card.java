package gamePieces;

import javafx.scene.Cursor;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
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

	private boolean isFaceUp;

	public Card() {}

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
		Text cardNameText = new Text(cardName);
		cardNameText.setWrappingWidth(60);
		cardNameText.setTranslateY(15);
		cardNameText.setTranslateX(5);
		this.getChildren().add(cardNameText);

		this.setCursor(Cursor.HAND);

		MouseEventHandler mouseEventHandler = new MouseEventHandler();

		//this.setOnMouseClicked ( mouseEventHandler );
		this.setOnMouseDragged ( mouseEventHandler );
		this.setOnMousePressed ( mouseEventHandler );
		this.setOnMouseReleased( mouseEventHandler );

		this.setOnScroll(new ScrollEventHandler());

		this.requestFocus();
		this.setFocused(true);

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
		private double cardGrepPointX;
		private double cardGrepPointY;
		private EventType<? extends MouseEvent> lastEvent;

		@Override
		public void handle(MouseEvent event) {
			if( event.getEventType() == MouseEvent.MOUSE_PRESSED ) {
				this.cardGrepPointX = event.getX();
				this.cardGrepPointY = event.getY();
				Card.this.setCursor(Cursor.MOVE);
			}

			if( event.getEventType() == MouseEvent.MOUSE_RELEASED) {
				Card.this.setCursor(Cursor.HAND);
				if( this.lastEvent == MouseEvent.MOUSE_PRESSED ) {
					if(Card.this.getRotate() == 0) {
						Card.this.setRotate(180d);
					} else {
						Card.this.setRotate(0d);
					}
				}
			}

			if( event.getEventType() == MouseEvent.MOUSE_DRAGGED ) {
				//if(Card.this.getRotate() == 0) {
				if(true) {
					Card.this.setTranslateX( Card.this.getTranslateX() + event.getX() - cardGrepPointX );
					Card.this.setTranslateY( Card.this.getTranslateY() + event.getY() - cardGrepPointY );
					System.out.print(" trX: " + Card.this.getTranslateX());
					System.out.print(" evX: " + event.getX());
					System.out.println(" evY: " + event.getY());
				} else {
					Card.this.setTranslateX(Card.this.getTranslateX() + event.getX() /* - cardGrepPointX */);
					Card.this.setTranslateY(Card.this.getTranslateY() + event.getY() /* - cardGrepPointY */);
					System.out.print(" trX: " + Card.this.getTranslateX());
					System.out.print(" evX: " + event.getX());
					System.out.println(" evY: " + event.getY());
				}
			}

			this.lastEvent = event.getEventType();
		}
	}

	private class ScrollEventHandler implements EventHandler<ScrollEvent> {
		@Override
		public void handle(ScrollEvent event) {
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
