package gamePieces;

import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;

import graphicsObjects.CardData;

import javafx.event.EventHandler;
import javafx.event.EventType;

public class Card extends MovableGamePiece {
	public static final double HEIGHT = 180;
	public static final double WIDTH = 126;

	private String cardName;
	private String type;
	private String subtype;
	private String ability;
	private String flavour;

	private int power;
	private int toughness;
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

	private int convertedManaCost;


	/**
	 * The cards margin while it is in hand <br>
	 * TODO this should be replaced with better card position logic
	 * for the hand
	 */
	private int preferdMargin;


	/**
	 * used in playes SendCardDataThread to check if it's safe
	 * to let the card go into a collection, among other possible
	 * future uses.
	 */
	//private boolean beingUsed;

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
		int manaCostBlank
	) {
		super( WIDTH, HEIGHT, Collections.DECK );
		pieceId = PIECE_ID_COUNTER_NEW++;

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

	}

	/**
	 * Used to allow for copying a card
	 * @param cardToCopy the card that you want a copy of
	 * @param cardId the id the new card should have
	 * you have to make sure yourself that no two cards share an id number
	 * in the local scope desired
	 */
	public Card( Card cardToCopy, long pieceId ) {
		super( WIDTH, HEIGHT, cardToCopy.getCurrentLocation() );
		this.pieceId = pieceId;

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

		this.preferdMargin = 25;

		//===============================//
		//         JavaFX below          //
		//===============================//
		
		this.getStyleClass().add("card");

		//this.setHeight(Card.HEIGHT);
		//this.setWidth(Card.WIDTH);
		//this.setMinSize(this.getWidth(), this.getHeight());
		//this.setPrefSize(this.getWidth(), this.getHeight());

		this.getChildren().add( new CardData(this) );

		this.setCursor(Cursor.HAND);

		mouseEventHandler = new MouseEventHandler();

		// TODO these are only applicable for the own cards
		this.addEventHandler( MouseEvent.MOUSE_ENTERED, mouseEventHandler );
		this.addEventHandler( MouseEvent.MOUSE_EXITED, mouseEventHandler );
		this.addEventHandler( MouseEvent.MOUSE_PRESSED, mouseEventHandler );
		this.addEventHandler( MouseEvent.MOUSE_RELEASED, mouseEventHandler );
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

		//private double mouseInSceneX;
		//private double mouseInSceneY;
		private EventType<? extends MouseEvent> lastEvent;

		@Override
		public void handle(MouseEvent event) {
			switch( currentLocation ) {
			case BATTLEFIELD:
				// Rotates the card if it's clicked and not draged
				if( event.getEventType() == MouseEvent.MOUSE_RELEASED &&
					this.lastEvent == MouseEvent.MOUSE_PRESSED ) {
					if( getRotate() == 0 ) {
						smoothSetRotate( 90d, 500 );
					} else {
						smoothSetRotate( 0d, 500 );
					}
				}
				this.lastEvent = event.getEventType();

				break;
			case HAND:
				if( event.getEventType() == MouseEvent.MOUSE_ENTERED ) {
					smoothPlace( getTranslateX(), -3*handPopupValue, 200 );
				}

				if( event.getEventType() == MouseEvent.MOUSE_EXITED ) {
					smoothPlace( getTranslateX(), handPopupValue, 200 );
				}
				break;
			default:
				break;
			}
		}
	}


	public static Card getCurrentCard() {
		return (Card) getCurrentPiece();
	}


	public int getPreferdMargin() {
		return preferdMargin;
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
			"manaCostBlank: " + manaCostBlank + "\n";
		return returnString;
	}
	
	/**
	* @return the cardId
	*/
	public long getCardId() {
		return getPieceId();
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
