package graphicsObjects;

import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class DeckPane extends StackPane {
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
