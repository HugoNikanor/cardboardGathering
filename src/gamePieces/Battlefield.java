package gamePieces;

import database.JSONCardReader;

import exceptions.CardNotFoundException;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * A battlefield in rule design Also the graphical representation of the same
 */
public class Battlefield extends Pane {

	private Player player;

	/**
	 * Here so that all cards can have a set height in relation to everything else.
	 */
	private Pane cardLayer;

	private BorderPane upperPane;
	private BorderPane lowerPane;

	private static final double WIDTH = 1920;// 1600;
	private static final double HEIGHT = 474;// 395;

	private boolean isReady;

	/**
	 * This is for creating the local battlefield
	 *
	 * @param jCardReader
	 *            Get's the card data from there
	 * @param cardList
	 *            String[] with all card names to use in it
     * @param local
	 *            if the battlefield is the local battlefield, or the oponenents
	 *            battlefield, which is gotten over the network.
	 */
	public Battlefield( JSONCardReader jCardReader, String[] cardList, boolean local ) {
		if( local )
			player = new Player( WIDTH, jCardReader, new CardPlayHandler(), cardList );
		else
			player = new Player( jCardReader, cardList );

		this.initialSetup( local );
	}

	/**
	 * The parts of the constructor that are the same between the local and
	 * remote instance of the class <br>
	 * TODO replace as much of this as possible with fxml files
	 */
	private void initialSetup( boolean isLocal ) {
		this.getStyleClass().add("battlefield");
		this.setWidth(WIDTH);
		this.setHeight(HEIGHT);
		this.setMinHeight(HEIGHT);
		this.setMaxHeight(HEIGHT);
		this.setPrefSize(this.getWidth(), this.getHeight());
		this.setMinSize(this.getWidth(), this.getHeight());

		cardLayer = new Pane();
		cardLayer.setPickOnBounds( false );
		cardLayer.setPrefSize(this.getWidth(), this.getHeight());

		upperPane = new BorderPane();
		upperPane.setPickOnBounds( false );
		upperPane.setPrefSize( this.getWidth(), this.getHeight() );

		lowerPane = new BorderPane();
		lowerPane.setPickOnBounds( false );
		lowerPane.setPrefSize( this.getWidth(), this.getHeight() );

		VBox cardStackContainerContainer = new VBox( 30 );
		cardStackContainerContainer.setFillWidth( false );
		cardStackContainerContainer.setAlignment( Pos.CENTER );
		cardStackContainerContainer.setPadding( new Insets(20) );
		lowerPane.setRight( cardStackContainerContainer );

		HBox lifeAndChatContainerContainer = new HBox();
		lifeAndChatContainerContainer.setPickOnBounds( false );
		Pane lifeAndChatContainer = new Pane();
		lifeAndChatContainer.setPickOnBounds( false );

		lifeAndChatContainerContainer.getChildren().add( lifeAndChatContainer );
		lifeAndChatContainerContainer.setAlignment( Pos.CENTER_LEFT );
		upperPane.setBottom( lifeAndChatContainerContainer );

		this.getChildren().addAll( cardLayer, upperPane, lowerPane );
		upperPane.toFront();
		lowerPane.toBack();

		// token container 
		if( isLocal ) {
			this.getChildren().addAll( 
					player.getTokenContainer() );
		}

		// Deck & Graveyard
		cardStackContainerContainer.getChildren().addAll( 
				player.getGraveGraphic(isLocal), player.getDeckGraphic(isLocal) );

		// Life counter || chat box
		if( isLocal )
			lifeAndChatContainer.getChildren().add( player.getChatContainer() );
		else
			lifeAndChatContainer.getChildren().add( player.getLifecounter() );

		// used when sending the battlefield to the other player
		if( isLocal )
			this.isReady = true;

		if( !isLocal )
			this.setRotate( 180d );
	}

	/**
	 * Place a card on the graphical battlefield
	 */
	public void playCard( Card card ) {
		this.cardLayer.getChildren().add( card );
	}

	/**
	 * Update which scale the window is in Used by card to know how far to move
	 *
	 * @param newScaleFactor
	 *            the scalefactor that should be used from this point on
	 */
	public void updateScaleFactor(double newScaleFactor) {
		player.updateScaleFactor(newScaleFactor);
	}

	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * @return the cards
	 */
	public CardCollection getCards() {
		return player.getBattlefieldCards();
	}

	/**
	 * @return the isReady
	 */
	public boolean isReady() {
		return isReady;
	}

	/**
	 * EventHandler for cards being played, <br>
	 *
	 * This Handler is applied to the cards and is used when they are pressed
	 * and in a players hand. <br>
	 *
	 * TODO check if this can be placed further down the project
	 */
	private class CardPlayHandler implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) {
			try {
				Card tempCard = player
					.getHandCards()
					.getCard(
						(Card)event.getSource());

				if( tempCard.getCurrentLocation() == CardCollection.CollectionTypes.HAND ) {

					if( event.getEventType() == MouseEvent.MOUSE_CLICKED ) {
						player.playCard(tempCard, Battlefield.this);
					}

				}
			} catch (CardNotFoundException e) {
				// This will fail when using a card on the battlefield.
				// Be aware that it's the reason for not doing anything here
			}
		}
	}



}
