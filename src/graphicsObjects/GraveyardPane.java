package graphicsObjects;

import javafx.scene.layout.StackPane;

public class GraveyardPane extends StackPane {
	
	public GraveyardPane( double width, double height, double xPos, double yPos ) {
		this.setPrefSize( width, height );

		this.getStyleClass().add("graveyard");
		this.setTranslateX( xPos - width  - 10 );
		this.setTranslateY( yPos );
	}

}
