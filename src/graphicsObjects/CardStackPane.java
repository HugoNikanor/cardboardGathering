package graphicsObjects;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

/**
 * The graphical representation of a cardCollection <br>
 * This should have some way to differentiate the type of collection it is
 */
public class CardStackPane extends StackPane {

	/**
	 * The text displayed on the pane,
	 * use this for the number of cards in the collection
	 */
	private Text deckCardText;

	/**
	 * Don't create this by itself
	 * @param handler the event hadler for pressing the pane
	 * @param width how wide the pane should be
	 * @param height how high the pane should be
	 */
	public CardStackPane(EventHandler<MouseEvent> handler, double width, double height) {

		this.setOnMouseClicked( handler );
		this.setPrefSize(width, height);

		deckCardText = new Text("");

		this.getChildren().add(deckCardText);
		this.getStyleClass().add("deck");
		//this.setTranslateX(xPos - width  - 10);
		//this.setTranslateY(yPos - height - 10);
		this.setTranslateX( 0 );
		this.setTranslateY( 0 );

	}

	/**
	 * Update the text on the pane
	 * @param newText the text that should be displayed
	 * @see deckCardText
	 */
	public void setText(String newText) {
		deckCardText.setText(newText);
	}

}
