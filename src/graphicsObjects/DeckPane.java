package graphicsObjects;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

/**
 * The graphical representation of a cardCollection <br>
 * TODO maybe merge this with GraveyardPane
 */
public class DeckPane extends StackPane {

	/**
	 * The text displayed on the pane,
	 * use this for the number of cards in the collection
	 */
	private Text deckCardText;

	/**
	 * @param handler the event hadler for pressing the pane
	 * @param initialText what the pane should say upon creation
	 * @param width how wide the pane should be
	 * @param height how high the pane should be
	 * @param xPos where the pane shold be on the x axis
	 * @param yPos where the pane shold be on the y ayis
	 * @see deckCardText
	 */
	public DeckPane(EventHandler<MouseEvent> handler, String initialText, double width, double height, double xPos, double yPos) {

		this.setOnMouseClicked( handler );
		this.setPrefSize(width, height);

		deckCardText = new Text(initialText);
		this.getChildren().add(deckCardText);
		this.getStyleClass().add("deck");
		//this.setTranslateX(xPos - width  - 10);
		//this.setTranslateY(yPos - height - 10);
		this.setTranslateX( xPos );
		this.setTranslateY( yPos );

	}

	/**
	 * Update the text on the pane
	 * @param newText the text that should be displayed
	 * @see deckCardText
	 */
	public void updateText(String newText) {
		deckCardText.setText(newText);
	}

}
