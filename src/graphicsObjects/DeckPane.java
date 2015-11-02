package graphicsObjects;

import java.io.Serializable;

import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class DeckPane 
	extends StackPane
	implements Serializable {

	private static final long serialVersionUID = 5034684916374169861L;
	private Text deckCardText;

	public DeckPane(String initialText, double width, double height, double xPos, double yPos) {
		this.setPrefSize(width, height);

		deckCardText = new Text(initialText);
		this.getChildren().add(deckCardText);
		this.getStyleClass().add("deck");
		this.setTranslateX(xPos - width  - 10);
		this.setTranslateY(yPos - height - 10);
	}

	public void updateText(String newText) {
		deckCardText.setText(newText);
	}

}
