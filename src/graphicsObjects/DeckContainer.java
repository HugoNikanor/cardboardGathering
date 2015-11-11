package graphicsObjects;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

/**
 * An object to keep the DeckPane and GetDeckCardBtn in <br>
 * TODO is HBox the best extend?
 */
public class DeckContainer extends Pane {
	private DeckPane deck;
	private Button getFromDeckBtn;
	
	public DeckContainer( EventHandler<MouseEvent> listener, String initialText, double width, double height, double xPos, double yPos ) {
		deck = new DeckPane(listener, initialText, (4 * width) / 5, height, 0, 0);

		getFromDeckBtn = new Button();
		getFromDeckBtn.setRotate( 90 );
		getFromDeckBtn.setText( "Get Card" );
		getFromDeckBtn.setTranslateX( width );

		this.setPrefWidth( width );
		this.setPrefHeight( height );
		this.setTranslateX( xPos );
		this.setTranslateY( yPos );

		this.getChildren().addAll(deck, getFromDeckBtn);
	}
	
	public void updateText( String newText ) {
		deck.updateText( newText );
	}
}
