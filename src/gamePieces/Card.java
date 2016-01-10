package gamePieces;

import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;

import controllers.CardController;

import graphicsObjects.Token;

import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import network.Connection;
import network.ConnectionPool;

import serverPackets.CardFocusPacket;
import serverPackets.TokenPacket;

public class Card extends StackPane {
	private long cardId;

	private String cardName;
	private String type;
	private String subtype;
	private String ability;
	private String flavour;

	/**
	 * '*' is -200<br>
	 * number format exception is -300 (weird things)<br>
	 * -100 is missing data from database (a card without this value)
	 */
	private int power;
	/**
	 * '*' is -200<br>
	 * number format exception is -300 (weird things)<br>
	 * -100 is missing data from database (a card without this value)
	 */
	private int toughness;
	/** This isn't even used... */
	private int loyalty;

	/**
	 * The different "colors" of mana, <br>
	 * also the "colors" a card can be
	 */
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
	private int manaCostX;

	private int convertedManaCost;

	// These are the boundries of the battlefield node that the card is located in
	// TODO Should these maybe be static...
	private double containerSizeX;
	private double containerSizeY;

	/**
	 * Used when draging the card if the screen is resized <br>
	 */
	private double scaleFactor;

	public static final double HEIGHT = 180;//150;
	public static final double WIDTH = 126;//105;

	/**
	 * Used as a check so only one card can be marked as current at a time, <br>
	 * Allows to demark the old "currentCard" as non current.
	 */
	private static Card currentCard;

	/**
	 * The current location of the card
	 */
	private CardCollection.Collections currentLocation;

	/**
	 * The cards margin while it is in hand <br>
	 * TODO this should be replaced with better card position logic
	 * for the hand
	 */
	private int preferdMargin;

	private Connection connection;
	private boolean shouldSend;

	// These three are used by the SendCardDataThread in player
	private double oldX;
	private double oldY;
	private double oldRotate;

	private ArrayList<Token> tokenAccess;

	/**
	 * used in playes SendCardDataThread to check if it's safe
	 * to let the card go into a collection, among other possible
	 * future uses.
	 */
	private boolean beingUsed;

	private MouseEventHandler mouseEventHandler;

	/**
	 * How far a card should pop up when you hoover over it
	 */
	private int handPopupValue = 20;


	/**
	 * Creates the general idea of the card
	 * This version is however not to be used directly
	 * but rather to be copied through the other constructor
	 * @param cardName name of the card
	 * @param type types of the card, separated by spaces
	 * @param subtype subtypes, separated by spaces
	 * @param ability abilties, preferably separated by linebreak
	 * @param flavour bonus text
	 * @param power the cards desired attack power TODO document special values
	 * @param toughness the cards "health"
	 * @param loyalty used by those who walk on the plains
	 * @param manaCostBlack mana needed to play the card
	 * @param manaCostBlue mana needed to play the card
	 * @param manaCostGreen mana needed to play the card
	 * @param manaCostRed mana needed to play the card
	 * @param manaCostWhite mana needed to play the card
	 * @param manaCostBlank mana needed to play the card
	 */
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
		int manaCostBlank,
		int manaCostX
	) {
		//cardId = CARD_ID_COUNTER_NEW++;

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
		this.manaCostX     = manaCostX;

		this.calcConvMana();

		currentLocation = CardCollection.Collections.DECK;
	}

	/**
	 * Used to allow for copying a card,<br>
	 * also allow the above "idea of card" to become
	 * a full fledged card, I'm sorry.
	 * @param cardToCopy the card that you want a copy of
	 * @param cardId the id the new card should have
	 * you have to make sure yourself that no two cards share an id number
	 * in the local scope desired
	 */
	public Card( Card cardToCopy, long cardId ) {
		this.cardId = cardId;

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
		this.manaCostX     = cardToCopy.getManaCostX();

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

		this.updateGraphics();

		this.setCursor(Cursor.HAND);

		mouseEventHandler = new MouseEventHandler();

		// TODO these are only applicable for the own cards
		this.setOnMouseDragged ( mouseEventHandler );
		this.setOnMousePressed ( mouseEventHandler );
		this.setOnMouseReleased( mouseEventHandler );
		this.setOnMouseEntered ( mouseEventHandler );
		this.setOnMouseExited  ( mouseEventHandler );
		this.setOnScroll( new ScrollEventHandler() );

		this.scaleFactor = 1;


		this.setFocusTraversable( true );

		this.connection = ConnectionPool.getConnection();



	}

	private void updateGraphics() {
		//Pane buffer = new CardController().getGraphics(this);
		try {
			URL url = Paths.get("fxml/CardDesign.fxml").toUri().toURL();
			//Pane buffer = FXMLLoader.load( url );
			FXMLLoader loader = new FXMLLoader( url );
			Pane buffer = loader.load();
			((CardController)loader.getController()).updateFields( this );
			this.getChildren().setAll( buffer );
		} catch( Exception e ) {
			e.printStackTrace();
		}
	}

	public void updateContainerSize() {
		try {
			containerSizeX = ((Pane) this.getParent()).getPrefWidth() - this.getWidth();
			containerSizeY = ((Pane) this.getParent()).getPrefHeight() - this.getHeight();
		} catch( NullPointerException e ) {
			containerSizeX = 0;
			containerSizeY = 0;
		}
	}

	/**
	 * Adds up all the mana costs <br>
	 * Automacicly asigns the output to 'convertedManaCost' <br>
	 * TODO this should have a special cause for lands
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


	public class MouseEventHandler implements EventHandler<MouseEvent> {

		private double mouseInSceneX;
		private double mouseInSceneY;
		private EventType<? extends MouseEvent> lastEvent;

		@Override
		public void handle(MouseEvent event) {

			
			switch( currentLocation ) {
			case BATTLEFIELD:

				// Give focus on hover
				if( event.getEventType() == MouseEvent.MOUSE_ENTERED ) {
					Card.this.giveFocus();
				}

				// Used to get a difference in mouse position
				if( event.getEventType() == MouseEvent.MOUSE_PRESSED ) {
					this.mouseInSceneX = event.getSceneX();
					this.mouseInSceneY = event.getSceneY();
				}

				// Set the cursor back to normal when not draging any more
				if( event.getEventType() == MouseEvent.MOUSE_RELEASED ) {
					beingUsed = false;
					Card.this.setCursor( Cursor.HAND );
				}

				// Rotates the card if it's clicked and not draged
				if( event.getEventType() == MouseEvent.MOUSE_RELEASED &&
					this.lastEvent == MouseEvent.MOUSE_PRESSED ) {
					if( getRotate() == 0 ) {
						smoothSetRotate( 90d, 500 );
					} else {
						smoothSetRotate( 0d, 500 );
					}
				}

				// Moves the card when it's draged by the mouse
				if( event.getEventType() == MouseEvent.MOUSE_DRAGGED ) {
					beingUsed = true;
					Card.this.setCursor(Cursor.MOVE);
					double xChange = event.getSceneX() - this.mouseInSceneX;
					double yChange = event.getSceneY() - this.mouseInSceneY;

					Card.this.setTranslateX(getTranslateX() + xChange * ( 1/scaleFactor ));
					Card.this.setTranslateY(getTranslateY() + yChange * ( 1/scaleFactor ));

					// Check that the card doesn't leave the field
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

				break;
			case HAND:
				if( event.getEventType() == MouseEvent.MOUSE_ENTERED ) {
					//smoothPlace( getTranslateX(), -3*handPopupValue, 200 );
					smoothPlaceY( -3 * handPopupValue, 200 );
				}

				if( event.getEventType() == MouseEvent.MOUSE_EXITED ) {
					//smoothPlace( getTranslateX(), handPopupValue, 200 );
					smoothPlaceY( handPopupValue, 200 );
				}
				break;
			default:
				break;
			}
		}
	}

	/**
	 * Zooms the card between two levels when the card is scrolled
	 * over
	 */
	private class ScrollEventHandler implements EventHandler<ScrollEvent> {
		@Override
		public void handle(ScrollEvent event) {
			if( event.getDeltaY() > 0 )
				smoothSetScale( 2.5, 50 );
			else
				smoothSetScale( 1, 50 );
		}
	}

	public void setToken( int tokenField, int newValue ) {
		tokenAccess.get( tokenField ).setNumber( newValue );
	}

	public void smoothSetScale( double scale ) {
		smoothSetScale( scale, 50 );
	}

	public void smoothSetScale( double scale, double duration ) {
		ScaleTransition st = new ScaleTransition( Duration.millis(duration), this );

		System.out.println( scale );
		st.setToX( scale );
		st.setToY( scale );

		st.play();
	}

	public void smoothSetRotate( double rotation ) {
		smoothSetRotate( rotation, 500 );
	}

	public void smoothSetRotate( double rotation, int duration ) {
		RotateTransition rt;
		rt = new RotateTransition(Duration.millis(duration), Card.this);
		rt.setToAngle(rotation);
		rt.play();
	}

	// TODO rewrite this to work as the other smoothMove methods
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

	public void giveFocus() {
		if( shouldSend && currentCard != this ) {
			try {
				currentCard.setId( null );
			} catch( NullPointerException e ) {
				// On the first go round nothing have been
				// asigned to currentCard yet
			}
			currentCard = this;

			connection.sendPacket( new CardFocusPacket(this.getCardId()) );
			this.requestFocus();
			this.setId( "has-focus" );
		}
		this.toFront();
	}

	/**
	 * Smoothly slides the card along,
	 * uses default movement speed of 30ms
	 * @param changeX how much the card should move horizontaly
	 * @param changeY how much the card should move verticly
	 */
	public void smoothMove( double changeX, double changeY ) {
		smoothMove( changeX, changeY, 30 );
	}
	/**
	 * Smoothly slides the card along
	 * @param changeX how much the card should move horizontaly
	 * @param changeY how much the card should move verticly
	 * @param moveSpeed how many milli secounds the move should last
	 */
	public void smoothMove( double changeX, double changeY, int moveSpeed ) {
		smoothPlace( getTranslateX() + changeX, getTranslateY() + changeY, moveSpeed );
	}

	/**
	 * Smoothly moves the card to the set coordinate <br>
	 * If the coordinate is out of bounds then it's set to the bound <br>
	 * This version uses the default transitionSpeed of 200
	 * @param posX the x coordinate the card should end up at
	 * @param posY the Y coordinate the card should end up at
	 */
	public void smoothPlace( double posX, double posY ) {
		smoothPlace( posX, posY, 200 );
	}

	/**
	 * Smoothly moves the card to the set coordinate
	 * @param posX the x coordinate the card should end up at
	 * @param posY the Y coordinate the card should end up at
	 * @param transitionSpeed how long the animation should take, in milliseconds
	 */
	public void smoothPlace( double posX, double posY, int transitionSpeed ) {
		TranslateTransition tt;
		tt = new TranslateTransition( Duration.millis( transitionSpeed ), this );
		tt.setToX( posX );
		tt.setToY( posY );
		tt.play();
	}
	public void smoothPlaceY( double posY, int transitionSpeed ) {
		TranslateTransition tt;
		tt = new TranslateTransition( Duration.millis( transitionSpeed ), this );

		tt.setToY( posY );

		tt.play();
	}
	public void smoothPlaceY( double posY ) {
		smoothPlaceY( posY, 200 );
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
	 * @param scaleFactor the scaleFactor to set
	 */
	public void setScaleFactor(double scaleFactor) {
		this.scaleFactor = scaleFactor;
	}

	public static Card getCurrentCard() {
		return currentCard;
	}

	public CardCollection.Collections getCurrentLocation() {
		return currentLocation;
	}

	public void setCurrentLocation(CardCollection.Collections currentLocation) {
		this.currentLocation = currentLocation;
	}

	public int getPreferdMargin() {
		return preferdMargin;
	}

	/**
	 * @param connection the connection to set
	 */
	/*
	public void setConnection(Connection connection) {
		//new Thread( new SendDataThread() ).start();
		this.shouldSend = true;
		this.connection = connection;
	}
	*/

	/**
	 * @return the shouldSend
	 */
	public boolean isShouldSend() {
		return shouldSend;
	}

	/**
	 * @param shouldSend the shouldSend to set
	 */
	public void setShouldSend(boolean shouldSend) {
		this.shouldSend = shouldSend;

		for( Token t : tokenAccess ) {
			t.getNumberProperty().addListener( (ov, oVal, nVal) -> {
				connection.sendPacket( new TokenPacket(
							tokenAccess.indexOf(t), nVal.intValue(), getCardId() ));
			});
		}
	}

	/**
	 * @return the oldX
	 */
	public double getOldX() {
		return oldX;
	}

	/**
	 * @param oldX the oldX to set
	 */
	public void setOldX(double oldX) {
		this.oldX = oldX;
	}

	/**
	 * @return the oldY
	 */
	public double getOldY() {
		return oldY;
	}

	/**
	 * @param oldY the oldY to set
	 */
	public void setOldY(double oldY) {
		this.oldY = oldY;
	}

	/**
	 * @return the oldRotate
	 */
	public double getOldRotate() {
		return oldRotate;
	}

	/**
	 * @param oldRotate the oldRotate to set
	 */
	public void setOldRotate(double oldRotate) {
		this.oldRotate = oldRotate;
	}

	/**
	 * @return the tokenAccess
	 */
	public ArrayList<Token> getTokenAccess() {
		return tokenAccess;
	}

	/**
	 * @param tokenAccess the tokenAccess to set
	 */
	public void setTokenAccess(ArrayList<Token> tokenAccess) {
		this.tokenAccess = tokenAccess;
	}

	/**
	 * @return the beingUsed
	 */
	public boolean isBeingUsed() {
		return beingUsed;
	}

	/**
	 * @param beingUsed the beingUsed to set
	 */
	public void setBeingUsed(boolean beingUsed) {
		this.beingUsed = beingUsed;
	}

	/**
	 * @return the mouseEventHandler
	 */
	public MouseEventHandler getMouseEventHandler() {
		return mouseEventHandler;
	}

	/**
	 * @return the handPopupValue
	 */
	public int getHandPopupValue() {
		return handPopupValue;
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
			"manaCostBlank: " + manaCostBlank + "\n" +
			"id           : " + cardId;
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

	/**
	 * @return the manaCostX
	 */
	public int getManaCostX() {
		return manaCostX;
	}

	public int getConvertedManaCost() {
		return convertedManaCost;
	}

}
