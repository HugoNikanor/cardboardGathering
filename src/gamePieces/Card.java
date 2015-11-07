package gamePieces;

import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import java.util.Objects;

import inputObjects.CardMoveObject;

import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import network.Connection;

public class Card extends Pane {
	private long cardId;
	private static long CARD_ID_COUNTER_NEW = 0;
	private static long CARD_ID_COUNTER_COPY = 0;

	private String cardName;
	private String type;
	private String subtype;
	private String ability;
	private String flavour;

	private int power;
	private int toughness;
	private int loyalty;

	enum ManaTypes {
		BLANK,
		BLACK,
		BLUE,
		GREEN,
		RED,
		WHITE,
	}
	private int manaCostBlank;
	private int manaCostBlack;
	private int manaCostBlue;
	private int manaCostGreen;
	private int manaCostRed;
	private int manaCostWhite;

	private int convertedManaCost;

	// These are the boundries of the battlefield node that the card is located in
	// TODO Should these maybe be static...
	private double containerSizeX;
	private double containerSizeY;

	private double scaleFactor;

	public static final double HEIGHT = 180;//150;
	public static final double WIDTH = 126;//105;

	private static Card currentCard;

	public static enum CardLocation {
		HAND,
		BATTLEFIELD,
		DECK,
		GRAVEYARD
	}
	private CardLocation currentLocation;

	// Used for the margin on one side of the card
	// the margins of two cards shouldn't overlap
	private int preferdMargin;

	private Connection connection;

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
		cardId = CARD_ID_COUNTER_NEW++;

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

		currentLocation = CardLocation.DECK;

		this.preferdMargin = 25;

		//===============================//
		//         JavaFX below          //
		//===============================//
		
		this.getStyleClass().add("card");

		this.setHeight(Card.HEIGHT);
		this.setWidth(Card.WIDTH);
		this.setMinSize(this.getWidth(), this.getHeight());
		this.setPrefSize(this.getWidth(), this.getHeight());

		Text cardNameText = new Text(cardName);
		cardNameText.setWrappingWidth(95);
		cardNameText.setTranslateY(15);
		cardNameText.setTranslateX(5);

		this.getChildren().add(cardNameText);

		this.setCursor(Cursor.HAND);

		currentCard = this;

		MouseEventHandler mouseEventHandler = new MouseEventHandler();

		this.setOnMouseDragged ( mouseEventHandler );
		this.setOnMousePressed ( mouseEventHandler );
		this.setOnMouseReleased( mouseEventHandler );

		this.setOnScroll( new ScrollEventHandler() );

		this.scaleFactor = 1;

		// if there is a better way to do this, tell me
		containerSizeX = Battlefield.WIDTH - this.getWidth();
		containerSizeY = Battlefield.HEIGHT - this.getHeight();

		//System.out.println("debug: end of Card");
	}

	/**
	 * Used to allow for copying a card
	 */
	public Card( Card cardToCopy ) {
		//System.out.println("debug: start of Card");
		cardId = CARD_ID_COUNTER_COPY++;
		
		//cardId = cardToCopy.getCardId();

		this.cardName  = cardToCopy.getCardName();
		this.type      = cardToCopy.getType();
		this.subtype   = cardToCopy.getSubtype();
		this.ability   = cardToCopy.getAbility();
		this.flavour   = cardToCopy.getFlavour();
		this.power     = cardToCopy.getPower();
		this.toughness = cardToCopy.getToughness();
		this.loyalty   = cardToCopy.getLoyalty();

        this.manaCostBlack = cardToCopy.getManaCostBlack();
        this.manaCostBlue  = cardToCopy.getManaCostBlue();
        this.manaCostGreen = cardToCopy.getManaCostGreen();
		this.manaCostRed   = cardToCopy.getManaCostRed();
        this.manaCostWhite = cardToCopy.getManaCostWhite();
		this.manaCostBlank = cardToCopy.getManaCostBlank();

		this.calcConvMana();

		currentLocation = cardToCopy.getCurrentLocation();

		this.preferdMargin = 25;

		//===============================//
		//         JavaFX below          //
		//===============================//
		
		this.getStyleClass().add("card");

		this.setHeight(Card.HEIGHT);
		this.setWidth(Card.WIDTH);
		this.setMinSize(this.getWidth(), this.getHeight());
		this.setPrefSize(this.getWidth(), this.getHeight());

		Text cardNameText = new Text(cardName);
		cardNameText.setWrappingWidth(95);
		cardNameText.setTranslateY(15);
		cardNameText.setTranslateX(5);

		this.getChildren().add(cardNameText);

		this.setCursor(Cursor.HAND);

		currentCard = this;

		MouseEventHandler mouseEventHandler = new MouseEventHandler();

		this.setOnMouseDragged ( mouseEventHandler );
		this.setOnMousePressed ( mouseEventHandler );
		this.setOnMouseReleased( mouseEventHandler );

		this.setOnScroll( new ScrollEventHandler() );

		this.scaleFactor = 1;

		// if there is a better way to do this, tell me
		containerSizeX = Battlefield.WIDTH - this.getWidth();
		containerSizeY = Battlefield.HEIGHT - this.getHeight();

		//System.out.println("debug: end of Card");
	}

	public static void resetCardIdCounter() {
		CARD_ID_COUNTER_COPY = 0;
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

		ManaTypes largestField = ManaTypes.BLANK;
		int largestValue = 0;
		if( manaCostBlack > largestValue ) {
			largestField = ManaTypes.BLACK;
			largestValue = manaCostBlack;
		}
		if( manaCostBlue > largestValue ) {
			largestField = ManaTypes.BLUE;
			largestValue = manaCostBlue;
		}
		if( manaCostGreen > largestValue ) {
			largestField = ManaTypes.GREEN;
			largestValue = manaCostGreen;
		}
		if( manaCostRed > largestValue ) {
			largestField = ManaTypes.RED;
			largestValue = manaCostRed;
		}
		if( manaCostWhite > largestValue ) {
			largestField = ManaTypes.WHITE;
			largestValue = manaCostWhite;
		}
		switch( largestField ) {
			case BLANK:
				this.getStyleClass().add("color-less");
			break;
			case BLACK:
				this.getStyleClass().add("black");
			break;
			case BLUE:
				this.getStyleClass().add("blue");
			break;
			case GREEN:
				this.getStyleClass().add("green");
			break;
			case RED:
				this.getStyleClass().add("red");
			break;
			case WHITE:
				this.getStyleClass().add("white");
			break;
		}
	}

	private class SendDataThread implements Runnable {
		private double oldX;
		private double oldY;

		@Override
		public void run() {
			synchronized( this ) {
				while( true ){
					// Only send move data if it's sencible to do so
					if( Objects.equals( Card.this.currentLocation, Card.CardLocation.BATTLEFIELD ) ) {
						double changeX = getTranslateX() - oldX;
						double changeY = getTranslateY() - oldY;

						if( changeX != 0 || changeY != 0 ) {
							connection.sendPacket( new CardMoveObject( cardId, changeX, changeY ) );
						}
						oldX = getTranslateX();
						oldY = getTranslateY();
					}
					try {
						this.wait( Connection.UPDATE_TIME );
					} catch( InterruptedException e ) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private class MouseEventHandler implements EventHandler<MouseEvent> {

		private double mouseInSceneX;
		private double mouseInSceneY;
		private EventType<? extends MouseEvent> lastEvent;

		@Override
		public void handle(MouseEvent event) {
			if( currentLocation == CardLocation.BATTLEFIELD ) {
				if( event.getEventType() == MouseEvent.MOUSE_PRESSED ) {
					Card.this.giveThisFocus();

					this.mouseInSceneX = event.getSceneX();
					this.mouseInSceneY = event.getSceneY();

					Card.this.setCursor(Cursor.MOVE);
				}

				if( event.getEventType() == MouseEvent.MOUSE_PRESSED ) {
					Card.this.setCursor(Cursor.HAND);
				}

				// Rotates the card if it's clicked and not draged
				if( event.getEventType() == MouseEvent.MOUSE_RELEASED &&
					this.lastEvent == MouseEvent.MOUSE_PRESSED ) {
					Card.this.smoothRotate(90d);
					//Card.this.smoothFlip(-180d);
				}

				/*
				if( event.getEventType() == MouseEvent.MOUSE_PRESSED ) {
					Card.this.setCursor(Cursor.HAND);
				}
				*/

				/**
				 * Moves the card when it's draged by the mouse
				 */
				if( event.getEventType() == MouseEvent.MOUSE_DRAGGED ) {
					double xChange = event.getSceneX() - this.mouseInSceneX;
					double yChange = event.getSceneY() - this.mouseInSceneY;

					Card.this.setTranslateX(getTranslateX() + xChange * ( 1/scaleFactor ));
					Card.this.setTranslateY(getTranslateY() + yChange * ( 1/scaleFactor ));

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
				}

				this.lastEvent = event.getEventType();
			} // end of BATTLEFIELD listeners

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
			 *
			 * TODO This should have a fancy animation,
			 * It also should have a locked max and min size 
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

	/********************************************
	 * Functions for manipulating the physical  *
	 * represontation of the card               *
	 ********************************************/

	/**
	 * Rotates the card to 'rotation' if the rotation is 0
	 * if the rotation isn't 0 the change it to zero
	 */
	public void smoothRotate( double rotation ) {
		RotateTransition rt;
		if( Card.this.getRotate() == 0 ) {
			rt = new RotateTransition(Duration.millis(500), Card.this);
			rt.setByAngle(rotation);
			rt.play();
		} else {
			rt = new RotateTransition(Duration.millis(500), Card.this);
			rt.setByAngle( -1 * Card.this.getRotate() );
			rt.play();
		}
	}
	public void smoothFlip( double rotation ) {
		RotateTransition rt;
		if( Card.this.getRotate() == 0 ) {
			// Rotates the card by 90 degrees
			// Always rotates clockwise
			rt = new RotateTransition(Duration.millis(300), Card.this);
			rt.setAxis(Rotate.Y_AXIS);
			rt.setByAngle(rotation);
			rt.play();
		} else {
			// if the rotation isn't 90 degrees then return to 0 degrees
			// always rotates counter clockwise
			rt = new RotateTransition(Duration.millis(300), Card.this);
			rt.setAxis(Rotate.Y_AXIS);
			rt.setByAngle( -1 * Card.this.getRotate() );
			rt.play();
		}
	}

	public void giveThisFocus() {
		currentCard.setId(null);
		currentCard = this;
		currentCard.setId("has-focus");
	}

	/**
	 * Smoothly slides the card along
	 */
	public void smoothMove( double changeX, double changeY ) {
		smoothMove( changeX, changeY, 30 );
	}
	/**
	 * Smoothly slides the card along
	 */
	public void smoothMove( double changeX, double changeY, int moveSpeed ) {

		TranslateTransition tt;
		tt = new TranslateTransition( Duration.millis( moveSpeed ), this );

		if( changeX > 0 ) {
			if( this.getTranslateX() + changeX > Battlefield.WIDTH - this.getWidth() ) {
				tt.setByX( Battlefield.WIDTH - this.getWidth() - this.getTranslateX() );
			} else {
				tt.setByX( changeX );
			}
		}

		if( changeY > 0 ) {
			if( this.getTranslateY() + changeY > Battlefield.HEIGHT - this.getHeight() ) {
				tt.setByY( Battlefield.HEIGHT - this.getHeight() - this.getTranslateY() );
			} else {
				tt.setByY( changeY );
			}
		}

		if( changeX < 0 ) {
			if( this.getTranslateX() + changeX < 0 ) {
					tt.setByX( -this.getTranslateX() );
			} else {
					tt.setByX( changeX );
			}
		}

		if( changeY < 0 ) {
			if( this.getTranslateY() + changeY < 0 ) {
				tt.setByY( -this.getTranslateY() );
			} else {
				tt.setByY( changeY );
			}
		}

		tt.play();
	}

	/**
	 * Smoothly moves the card to the set coordinate
	 * If the coordinate is out of bounds then it's set to the bound
	 */
	public void smoothPlace( double posX, double posY ) {
		smoothPlace( posX, posY, 200 );
	}
	/**
	 * Smoothly moves the card to the set coordinate
	 * If the coordinate is out of bounds then it's set to the bound
	 */
	public void smoothPlace( double posX, double posY, int transitionSpeed ) {

		TranslateTransition tt;
		tt = new TranslateTransition( Duration.millis( transitionSpeed ), this );

		if( posX > Battlefield.WIDTH - this.getWidth() ) {
			tt.setToX( Battlefield.WIDTH - this.getWidth() );
		} else {
			tt.setToX( posX );
		}

		if( posY > Battlefield.HEIGHT - this.getHeight() ) {
			tt.setToY( Battlefield.HEIGHT - this.getHeight() );
		} else {
			tt.setToY( posY );
		}

		tt.play();
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
	 * @param scaleFactorY the scaleFactorY to set
	 */
	public void setScaleFactor(double scaleFactor) {
		this.scaleFactor = scaleFactor;
	}

	public static Card getCurrentCard() {
		return currentCard;
	}

	public CardLocation getCurrentLocation() {
		return currentLocation;
	}

	public void setCurrentLocation(CardLocation currentLocation) {
		this.currentLocation = currentLocation;
	}

	public int getPreferdMargin() {
		return preferdMargin;
	}

	/**
	 * @param connection the connection to set
	 */
	public void setConnection(Connection connection) {
		new Thread( new SendDataThread() ).start();
		this.connection = connection;
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
	
	/**
	* @return the cardId
	*/
	public long getCardId() {
		return cardId;
	}

	/* *************************************
	 * Thread no futher, there is nothing 
	 * here but the vilest of getters ahead.
	 ***************************************/

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
