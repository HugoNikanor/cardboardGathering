package graphicsObjects;

import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

/**
 * This might be used for the card creation menu,
 * If it isn't used for that then this should be removed.
 */
public class TokenContainer extends HBox {

	private VBox drawer;
	private Pane handle;

	private double xPosIn;
	private double xPosOut;
	private double yPos;

	private double height;
	private double drawerWidth;
	private double handleWidth;

	private boolean drawerOut;

	private VBox databaseCreatePane;
	private HBox databaseInputPane;
	private StackPane databaseErrorPane;

	private Text errorText;
	private TextField cardDatabaseField;
	private Button cardDatabaseBtn;

	private VBox cardCreatePane;
	private Text cardCreateInfo;
	private TextField cardCreateNameField;
	private TextArea cardCreateInfoArea;
	private Button cardCreateAction;

	HBox cardCreatePaneContainer;

	public TokenContainer( EventHandler<ActionEvent> cardCreateHandler ) {
		height = 300;
		drawerWidth = 170;
		handleWidth = 35;


		// Error text displays if the requested card don't exist
		errorText = new Text();
		errorText.setUnderline( true );
		errorText.setFont( new Font(20) );

		// holds the error text
		databaseErrorPane = new StackPane();
		databaseErrorPane.setPrefWidth( drawerWidth );
		databaseErrorPane.getChildren().add( errorText );

		// the input for cards to request
		cardDatabaseField = new TextField();
		cardDatabaseField.setPromptText( "Querry database" );
		cardDatabaseField.setOnAction( cardCreateHandler );

		// request button, <enter> on field also works
		cardDatabaseBtn = new Button( ">" );
		cardDatabaseBtn.setOnAction( cardCreateHandler );

		// pane to hold the text field and button seen above
		databaseInputPane = new HBox();
		databaseInputPane.setFillHeight( true );
		databaseInputPane.getChildren().addAll( cardDatabaseField,
		                                        cardDatabaseBtn );


		// pane holding the pane for the input, as well as
		// the pane housing the error messages  
		databaseCreatePane = new VBox( 10 );
		databaseCreatePane.setFillWidth( true );
		databaseCreatePane.getChildren().addAll( databaseInputPane,
		                                         databaseErrorPane );



		// text informing the player about the function of the pane
		cardCreateInfo = new Text( "Create Card" );
		cardCreateInfo.setFont( new Font(17) );

		// text field for entering of the cards name
		cardCreateNameField = new TextField();
		cardCreateNameField.setPromptText( "Name:" );
		cardCreateNameField.setOnAction( cardCreateHandler );
		cardCreateNameField.getStyleClass().add( "card-create-name-field" );

		// text area for entering the body text of the card.
		// Note that there aren't any way to enter mana, 
		// damage or toughness directly
		cardCreateInfoArea = new TextArea();
		cardCreateInfoArea.setPromptText( "Card data:" );
		cardCreateInfoArea.setWrapText( true );
		cardCreateInfoArea.getStyleClass().add( "card-create-info-area" );

		// button for creating the card, <enter> in the name field 
		// also works
		cardCreateAction = new Button( "Create Card" );
		cardCreateAction.setOnAction( cardCreateHandler );

		// pane housing all the parts for creating a card
		cardCreatePane = new VBox();
		cardCreatePane.getStyleClass().add( "card-create-pane" );
		cardCreatePane.setAlignment( Pos.CENTER );
		cardCreatePane.setFillWidth( true );
		cardCreatePane.setPadding( new Insets(0, 5, 0, 5) );
		cardCreatePane.getStyleClass().add("card-create-pane");
		cardCreatePane.getChildren().addAll( cardCreateInfo,
		                                     cardCreateNameField,
		                                     cardCreateInfoArea,
		                                     cardCreateAction );
	
		// pane for easily positioning of the card create pane
		cardCreatePaneContainer = new HBox();
		cardCreatePaneContainer.getChildren().add( cardCreatePane );
		cardCreatePaneContainer.setPadding( new Insets(0, 10, 10, 10) );
		cardCreatePane.setAlignment( Pos.CENTER );


		// pane for holding the different card creation panes 
		drawer = new VBox( 20 );
		drawer.setAlignment( Pos.CENTER );
		drawer.setPrefWidth( drawerWidth );
		drawer.setMaxWidth( drawerWidth );
		drawer.setPrefHeight( height );
		drawer.getStyleClass().add( "token-container-drawer" );
		drawer.getChildren().addAll( databaseCreatePane,
		                             cardCreatePaneContainer );

		// a "handle" to the drawer, it looks pretty.
		// Press it to toggle if the drawer should be seen
		handle = new Pane();
		handle.setPrefWidth( handleWidth );
		handle.setPrefHeight( height );
		handle.getStyleClass().add( "token-container-handle" );
		handle.setOnMouseClicked( new HandleListener() );


		xPosOut = -5;
		xPosIn = -(drawerWidth + 15);
		yPos = 10;

		drawerOut = false;

		this.setTranslateX( xPosIn );
		this.setTranslateY( yPos );

		this.getStyleClass().add( "token-container" );
		this.getChildren().add( drawer );
		this.getChildren().add( handle );
	}

	public void clearTextArea() {
		cardDatabaseField.setText( "" );
	}
	
	public void openDrawer() {
		clearError();

		TranslateTransition tt = new TranslateTransition(Duration.millis(500), this);
		tt.setToX( xPosOut );
		drawerOut = true;
		this.toFront();
		tt.play();
	}

	public void closeDrawer() {
		TranslateTransition tt = new TranslateTransition(Duration.millis(500), this);
		tt.setToX( xPosIn );
		drawerOut = false;
		this.toBack();
		tt.play();
	}

	/**
	 * Displays an error message in the pane
	 * @param error the error to give
	 */
	public void error( String error ) {
		errorText.setText( "NO SUCH CARD" );
	}

	public void clearError() {
		errorText.setText( "" );
	}

	// TODO possibly add a mouse drag event to open and close the drawer
	private class HandleListener implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) {
			if( event.getEventType() == MouseEvent.MOUSE_CLICKED ) {
				if( drawerOut ) {
					closeDrawer();
				} else {
					openDrawer();
				}
			}
		}
	}	
}
