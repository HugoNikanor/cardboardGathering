package graphicsObjects;

import javafx.scene.layout.StackPane;

/**
 * Pane for representing the gaveyard cardcollection
 * TODO make it more general
 * TODO maybe merge this and DeckPane
 * @see graphicsObjects.DeckPane
 */
public class GraveyardPane extends StackPane {
	
	/**
	 * @param width how wide the pane should be
	 * @param height how high the pane should be
	 * @param xPos where the pane shold be on the x axis
	 * @param yPos where the pane shold be on the y ayis
	 */
	public GraveyardPane( double width, double height, double xPos, double yPos ) {
		this.setPrefSize( width, height );

		this.getStyleClass().add("graveyard");
		this.setTranslateX( xPos - width  - 10 );
		this.setTranslateY( yPos );
	}

}
